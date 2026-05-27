package cars.carbon.printService.production.quality.service;

import cars.carbon.printService.model.WorkOrders.WorkOrder;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.production.cutting.exceptions.ResourceNotFoundException;
import cars.carbon.printService.production.cutting.model.CuttingRecord;
import cars.carbon.printService.production.cutting.model.PlateConsumption;
import cars.carbon.printService.production.invoice.model.Invoice;
import cars.carbon.printService.production.invoice.model.PlateConsumptionInvoice;
import cars.carbon.printService.production.invoice.repository.InvoiceRepository;
import cars.carbon.printService.production.invoice.repository.PlateConsumptionInvoiceRepository;
import cars.carbon.printService.production.quality.dto.QualitySourceResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Agrega os dados de NF + corte + enfesto necessários para o Maestro
 * construir um Cert. de Qualidade pré-preenchido. Read-only.
 */
@Service
@RequiredArgsConstructor
public class QualitySourceService {

    private final InvoiceRepository invoiceRepository;
    private final PlateConsumptionInvoiceRepository plateConsumptionInvoiceRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public QualitySourceResponse findByInvoiceNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByNumberIgnoreCase(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NF " + invoiceNumber + " não encontrada"));

        // Todos os consumos faturados nesta NF (eager fetch do plateConsumption,
        // cuttingRecord e plate.workorder para evitar N+1).
        List<PlateConsumptionInvoice> rows = em.createQuery("""
                SELECT pci FROM PlateConsumptionInvoice pci
                  JOIN FETCH pci.plateConsumption pc
                  JOIN FETCH pc.cuttingRecord cr
                  LEFT JOIN FETCH pc.plate p
                  LEFT JOIN FETCH p.workorderid w
                 WHERE pci.invoice.id = :invoiceId
                """, PlateConsumptionInvoice.class)
                .setParameter("invoiceId", invoice.getId())
                .getResultList();

        if (rows.isEmpty()) {
            throw new ResourceNotFoundException(
                    "NF " + invoiceNumber + " não possui consumos vinculados");
        }

        // Todos os consumos devem pertencer ao mesmo cutting record — uma NF
        // não é dividida entre kits no modelo atual.
        CuttingRecord cr = rows.getFirst().getPlateConsumption().getCuttingRecord();

        BigDecimal total = BigDecimal.ZERO;
        List<QualitySourceResponse.Consumption> consumptions = new ArrayList<>();
        for (PlateConsumptionInvoice pci : rows) {
            PlateConsumption pc = pci.getPlateConsumption();
            Plates plate = pc.getPlate();
            WorkOrder wo = plate != null ? plate.getWorkorderid() : null;

            BigDecimal invoiced = pci.getUsedMetrage() != null
                    ? pci.getUsedMetrage()
                    : BigDecimal.ZERO;
            total = total.add(invoiced);

            QualitySourceResponse.Workorder woDto = wo != null
                    ? new QualitySourceResponse.Workorder(
                            wo.getId(),
                            wo.getLote(),
                            wo.getClothType(),
                            wo.getClothBatch(),
                            wo.getFabricSupplier())
                    : null;

            consumptions.add(new QualitySourceResponse.Consumption(
                    pc.getSupplier() != null ? pc.getSupplier().name() : null,
                    pc.getLayerQuantity(),
                    pc.getBatchNumber(),
                    invoiced,
                    woDto));
        }

        QualitySourceResponse.CuttingRecord crDto = new QualitySourceResponse.CuttingRecord(
                cr.getId(),
                cr.getOrderNumber(),
                cr.getOrderDescription(),
                cr.getMaterial() != null ? cr.getMaterial().name() : null,
                cr.getKitType() != null ? cr.getKitType().name() : null);

        return new QualitySourceResponse(
                invoice.getNumber(),
                crDto,
                total,
                consumptions);
    }
}
