package cars.carbon.printService.production.invoice.checklist;

import cars.carbon.printService.production.cutting.enums.SupplierType;
import cars.carbon.printService.production.cutting.exceptions.ResourceNotFoundException;
import cars.carbon.printService.production.cutting.model.CuttingRecord;
import cars.carbon.printService.production.cutting.model.PlateConsumption;
import cars.carbon.printService.production.cutting.repository.CuttingRecordRepository;
import cars.carbon.printService.production.invoice.document.DocumentType;
import cars.carbon.printService.production.invoice.document.InvoiceDocumentRepository;
import cars.carbon.printService.production.invoice.model.ConsumptionSplit;
import cars.carbon.printService.production.invoice.model.PlateConsumptionInvoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ComplianceChecklistService {

    private final CuttingRecordRepository cuttingRepo;
    private final InvoiceDocumentRepository documentRepo;

    public ComplianceChecklistResponse evaluate(Long cuttingRecordId) {

        CuttingRecord record = cuttingRepo.findById(cuttingRecordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Corte não encontrado: " + cuttingRecordId));

        List<ChecklistItemResponse> items = new ArrayList<>();

        for (PlateConsumption consumption : record.getConsumptions()) {

            List<PlateConsumptionInvoice> invoices = consumption.getInvoices() != null
                    ? consumption.getInvoices() : List.of();
            List<ConsumptionSplit> splits = consumption.getSplits() != null
                    ? consumption.getSplits() : List.of();
            String supplier = consumption.getSupplier() != null
                    ? consumption.getSupplier().name() : "N/A";

            boolean hasInvoices = !invoices.isEmpty();
            boolean hasSplits = !splits.isEmpty();
            boolean hasBilling = hasInvoices || hasSplits;

            // Regra 1: todo consumo deve ter ao menos uma NF apontada (ou divisões)
            items.add(new ChecklistItemResponse(
                    consumption.getId(),
                    supplier,
                    ChecklistRule.INVOICE_REQUIRED,
                    hasBilling
                            ? (hasSplits ? "Consumo dividido em " + splits.size() + " parte(s)" : "Nota fiscal apontada")
                            : "Consumo sem nota fiscal apontada",
                    hasBilling
            ));

            // Regra 2: fornecedor Opera exige documento NF anexado
            if (consumption.getSupplier() == SupplierType.OPERA && hasBilling) {

                Set<String> invoiceNumbers = Stream.concat(
                        invoices.stream().map(pci -> pci.getInvoice().getNumber()),
                        splits.stream().map(s -> s.getInvoice().getNumber())
                ).collect(Collectors.toSet());

                List<String> numbersList = List.copyOf(invoiceNumbers);
                boolean hasNfDoc = !documentRepo
                        .findActiveByInvoiceNumbersAndType(numbersList, DocumentType.NF)
                        .isEmpty();

                items.add(new ChecklistItemResponse(
                        consumption.getId(),
                        supplier,
                        ChecklistRule.NF_DOCUMENT_REQUIRED_OPERA,
                        hasNfDoc
                                ? "Documento NF anexado (Opera)"
                                : "Fornecedor Opera: documento NF obrigatório não anexado",
                        hasNfDoc
                ));
            }

            // Regra 3: soma das metragens faturadas deve ser igual ao consumo total
            if (hasBilling) {
                BigDecimal invoicedTotal = invoices.stream()
                        .map(PlateConsumptionInvoice::getUsedMetrage)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal splitsTotal = splits.stream()
                        .map(ConsumptionSplit::getUsedMetrage)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalFaturado = invoicedTotal.add(splitsTotal);

                boolean balanced = totalFaturado.compareTo(consumption.getUsedMetrage()) == 0;
                items.add(new ChecklistItemResponse(
                        consumption.getId(),
                        supplier,
                        ChecklistRule.QUANTITY_BALANCE,
                        balanced
                                ? "Saldo conferido: " + totalFaturado + " == " + consumption.getUsedMetrage()
                                : "Saldo divergente: faturado " + totalFaturado
                                  + " != consumo " + consumption.getUsedMetrage(),
                        balanced
                ));
            }
        }

        long failed = items.stream().filter(i -> !i.isPassed()).count();

        return new ComplianceChecklistResponse(
                record.getId(),
                record.getOrderNumber(),
                failed == 0,
                record.getConsumptions().size(),
                (int) failed,
                items
        );
    }
}
