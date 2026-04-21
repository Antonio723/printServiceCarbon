package cars.carbon.printService.production.invoice.aging;

import cars.carbon.printService.production.cutting.model.CuttingRecord;
import cars.carbon.printService.production.cutting.model.PlateConsumption;
import cars.carbon.printService.production.cutting.repository.CuttingRecordRepository;
import cars.carbon.printService.production.invoice.model.PlateConsumptionInvoice;
import cars.carbon.printService.production.invoice.repository.PlateConsumptionInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceAgingService {

    private final CuttingRecordRepository cuttingRepo;
    private final PlateConsumptionInvoiceRepository pciRepo;

    public AgingReportResponse generate() {

        List<CuttingRecord> records = cuttingRepo.findAllWithConsumptions();

        // Carrega vínculos de NF em lote para evitar N+1
        List<Long> consumptionIds = records.stream()
                .flatMap(r -> r.getConsumptions().stream())
                .map(PlateConsumption::getId)
                .toList();

        Map<Long, List<PlateConsumptionInvoice>> invoicesByConsumption = pciRepo
                .findByConsumptionIds(consumptionIds)
                .stream()
                .collect(Collectors.groupingBy(pci -> pci.getPlateConsumption().getId()));

        LocalDateTime now = LocalDateTime.now();
        List<AgingItemResponse> items = new ArrayList<>();

        for (CuttingRecord record : records) {

            List<PlateConsumption> unbilledConsumptions = record.getConsumptions().stream()
                    .filter(c -> invoicesByConsumption.getOrDefault(c.getId(), List.of()).isEmpty())
                    .toList();

            if (unbilledConsumptions.isEmpty()) continue;

            long days = ChronoUnit.DAYS.between(record.getCreatedAt(), now);
            AgingBucket bucket = AgingBucket.of(days);

            List<AgingItemResponse.UnbilledConsumption> unbilledList = unbilledConsumptions.stream()
                    .map(c -> new AgingItemResponse.UnbilledConsumption(
                            c.getId(),
                            c.getSupplier() != null ? c.getSupplier().name() : "N/A",
                            c.getUsedMetrage()
                    ))
                    .toList();

            items.add(new AgingItemResponse(
                    record.getId(),
                    record.getOrderNumber(),
                    record.getCreatedAt(),
                    days,
                    bucket,
                    record.getConsumptions().size(),
                    unbilledConsumptions.size(),
                    unbilledList
            ));
        }

        // Ordena: mais crítico e mais antigo primeiro
        items.sort(Comparator
                .comparing(AgingItemResponse::getBucket).reversed()
                .thenComparing(Comparator.comparing(AgingItemResponse::getDaysSinceCreation).reversed()));

        Map<AgingBucket, Long> summary = items.stream()
                .collect(Collectors.groupingBy(AgingItemResponse::getBucket, Collectors.counting()));

        return new AgingReportResponse(now, summary, items);
    }
}
