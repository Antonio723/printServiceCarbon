package cars.carbon.printService.production.invoice.service;

import cars.carbon.printService.production.cutting.exceptions.BusinessException;
import cars.carbon.printService.production.cutting.exceptions.ResourceNotFoundException;
import cars.carbon.printService.production.cutting.model.PlateConsumption;
import cars.carbon.printService.production.cutting.repository.CuttingRecordRepository;
import cars.carbon.printService.production.cutting.repository.PlateConsumptionRepository;
import cars.carbon.printService.production.invoice.dto.CuttingRecordInvoiceUpdateRequest;
import cars.carbon.printService.production.invoice.dto.CuttingRecordInvoiceUpdateRequest.ConsumptionDTO;
import cars.carbon.printService.production.invoice.dto.CuttingRecordInvoiceUpdateRequest.InvoiceDTO;
import cars.carbon.printService.production.invoice.dto.CuttingRecordInvoiceUpdateRequest.SplitDTO;
import cars.carbon.printService.production.invoice.model.ConsumptionSplit;
import cars.carbon.printService.production.invoice.model.Invoice;
import cars.carbon.printService.production.invoice.model.PlateConsumptionInvoice;
import cars.carbon.printService.production.invoice.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final CuttingRecordRepository cuttingRepo;
    private final PlateConsumptionRepository consumptionRepo;
    private final InvoiceRepository invoiceRepo;

    @Transactional
    public void update(CuttingRecordInvoiceUpdateRequest request) {

        cuttingRepo.findById(request.getCuttingRecordId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CuttingRecord não encontrado: " + request.getCuttingRecordId()));

        boolean isSingleInvoice = Boolean.TRUE.equals(request.getSingleInvoice());

        for (ConsumptionDTO consumptionDTO : request.getConsumptions()) {

            PlateConsumption consumption = consumptionRepo.findById(consumptionDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Consumo não encontrado: " + consumptionDTO.getId()));

            List<InvoiceDTO> invoiceDTOs = consumptionDTO.getInvoices() != null
                    ? consumptionDTO.getInvoices() : List.of();
            List<SplitDTO> splitDTOs = consumptionDTO.getSplits() != null
                    ? consumptionDTO.getSplits() : List.of();

            if (isSingleInvoice) {
                validateSingleInvoiceMode(consumptionDTO, consumption);
            } else {
                validateMutualExclusion(invoiceDTOs, splitDTOs, consumption.getId());
            }

            if (!splitDTOs.isEmpty()) {
                validateSplitMetrage(splitDTOs, consumption);
                applySplits(consumption, splitDTOs);
            } else {
                validateInvoiceMetrage(invoiceDTOs, consumption);
                applyInvoices(consumption, invoiceDTOs);
            }
        }
    }

    // ── Scenario 1: singleInvoice mode ──────────────────────────────────────

    private void validateSingleInvoiceMode(ConsumptionDTO dto, PlateConsumption consumption) {
        List<SplitDTO> splits = dto.getSplits() != null ? dto.getSplits() : List.of();
        if (!splits.isEmpty()) {
            throw new BusinessException(
                    "Modo 'Nota Única' ativo: divisões de consumo não são permitidas (consumo ID=" + consumption.getId() + ")");
        }
        List<InvoiceDTO> invoices = dto.getInvoices() != null ? dto.getInvoices() : List.of();
        if (invoices.size() > 1) {
            throw new BusinessException(
                    "Modo 'Nota Única' ativo: cada consumo deve ter no máximo 1 nota fiscal (consumo ID=" + consumption.getId() + ")");
        }
    }

    // ── Mutual exclusion: invoices XOR splits ────────────────────────────────

    private void validateMutualExclusion(List<InvoiceDTO> invoices, List<SplitDTO> splits, Long consumptionId) {
        if (!invoices.isEmpty() && !splits.isEmpty()) {
            throw new BusinessException(
                    "Consumo ID=" + consumptionId + ": não é possível combinar notas fiscais e divisões no mesmo consumo");
        }
    }

    // ── Metrage validations ──────────────────────────────────────────────────

    private void validateInvoiceMetrage(List<InvoiceDTO> invoices, PlateConsumption consumption) {
        BigDecimal total = invoices.stream()
                .map(InvoiceDTO::getUsedMetrage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(consumption.getUsedMetrage()) > 0) {
            throw new BusinessException(
                    "Consumo ID=" + consumption.getId() + ": soma das notas (" + total +
                    " mm) excede o consumo (" + consumption.getUsedMetrage() + " mm)");
        }
    }

    private void validateSplitMetrage(List<SplitDTO> splits, PlateConsumption consumption) {
        BigDecimal total = splits.stream()
                .map(SplitDTO::getUsedMetrage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(consumption.getUsedMetrage()) > 0) {
            throw new BusinessException(
                    "Consumo ID=" + consumption.getId() + ": soma das divisões (" + total +
                    " mm) excede o consumo (" + consumption.getUsedMetrage() + " mm)");
        }
    }

    // ── Apply invoices (Scenario 1 & 2) ─────────────────────────────────────

    private void applyInvoices(PlateConsumption consumption, List<InvoiceDTO> invoiceDTOs) {
        if (consumption.getInvoices() == null) {
            consumption.setInvoices(new ArrayList<>());
        } else {
            consumption.getInvoices().clear();
        }

        consumption.getSplits().clear();

        for (InvoiceDTO dto : invoiceDTOs) {
            Invoice invoice = getOrCreate(dto.getNumber());
            PlateConsumptionInvoice pci = new PlateConsumptionInvoice(
                    null, dto.getUsedMetrage(), consumption, invoice);
            consumption.getInvoices().add(pci);
        }
    }

    // ── Apply splits (Scenario 3) ────────────────────────────────────────────

    private void applySplits(PlateConsumption consumption, List<SplitDTO> splitDTOs) {
        consumption.getInvoices().clear();

        if (consumption.getSplits() == null) {
            consumption.setSplits(new ArrayList<>());
        } else {
            consumption.getSplits().clear();
        }

        for (SplitDTO dto : splitDTOs) {
            String number = dto.getInvoice() != null ? dto.getInvoice().getNumber() : null;
            if (number == null || number.isBlank()) {
                throw new BusinessException(
                        "Divisão sem número de nota fiscal no consumo ID=" + consumption.getId());
            }
            Invoice invoice = getOrCreate(number);
            ConsumptionSplit split = new ConsumptionSplit(
                    null, dto.getUsedMetrage(), invoice, consumption);
            consumption.getSplits().add(split);
        }
    }

    // ── Invoice lookup / creation ────────────────────────────────────────────

    private Invoice getOrCreate(String number) {
        if (number == null || number.isBlank()) {
            throw new BusinessException("Número da nota fiscal não pode ser vazio");
        }
        return invoiceRepo.findByNumber(number.trim())
                .orElseGet(() -> invoiceRepo.save(new Invoice(null, number.trim(), null, null)));
    }
}
