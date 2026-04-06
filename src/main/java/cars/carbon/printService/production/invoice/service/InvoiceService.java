package cars.carbon.printService.production.invoice.service;

import cars.carbon.printService.production.cutting.model.CuttingRecord;
import cars.carbon.printService.production.cutting.model.PlateConsumption;
import cars.carbon.printService.production.cutting.repository.CuttingRecordRepository;
import cars.carbon.printService.production.cutting.repository.PlateConsumptionRepository;
import cars.carbon.printService.production.invoice.dto.CuttingRecordInvoiceUpdateRequest;
import cars.carbon.printService.production.invoice.model.Invoice;
import cars.carbon.printService.production.invoice.model.PlateConsumptionInvoice;
import cars.carbon.printService.production.invoice.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final CuttingRecordRepository cuttingRepo;
    private final PlateConsumptionRepository consumptionRepo;
    private final InvoiceRepository invoiceRepo;

    @Transactional
    public void update(CuttingRecordInvoiceUpdateRequest request) {

        CuttingRecord record = cuttingRepo.findById(request.getCuttingRecordId())
                .orElseThrow(() -> new RuntimeException("CuttingRecord não encontrado"));

        for (var c : request.getConsumptions()) {

            PlateConsumption consumption = consumptionRepo.findById(c.getId())
                    .orElseThrow(() -> new RuntimeException("Consumo não encontrado"));

            // limpa vínculos antigos
            if (consumption.getInvoices() == null) {
                consumption.setInvoices(new ArrayList<>());
            } else {
                consumption.getInvoices().clear();
            }

            if (Boolean.TRUE.equals(request.getSingleInvoice())) {

                Invoice invoice = getOrCreate(request.getInvoiceNumber());

                PlateConsumptionInvoice pci = new PlateConsumptionInvoice(
                        null,
                        consumption.getUsedMetrage(),
                        consumption,
                        invoice
                );

                consumption.getInvoices().add(pci);

            } else {

                BigDecimal total = BigDecimal.ZERO;

                for (var inv : c.getInvoices()) {

                    Invoice invoice = getOrCreate(inv.getInvoiceNumber());

                    PlateConsumptionInvoice pci = new PlateConsumptionInvoice(
                            null,
                            inv.getUsedMetrage(),
                            consumption,
                            invoice
                    );

                    consumption.getInvoices().add(pci);
                    total = total.add(inv.getUsedMetrage());
                }

                // 🔥 VALIDAÇÃO CRÍTICA
                if (total.compareTo(consumption.getUsedMetrage()) != 0) {
                    throw new RuntimeException(
                            "Erro: soma das notas diferente do consumo ID=" + consumption.getId()
                    );
                }
            }
        }
    }

    private Invoice getOrCreate(String number) {
        return invoiceRepo.findByInvoiceNumber(number)
                .orElseGet(() -> invoiceRepo.save(new Invoice(null, number)));
    }
}