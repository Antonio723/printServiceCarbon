package cars.carbon.printService.production.invoice.aging;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AgingItemResponse {

    private Long cuttingRecordId;
    private String orderNumber;
    private LocalDateTime createdAt;
    private long daysSinceCreation;
    private AgingBucket bucket;
    private int totalConsumptions;
    private int unbilledConsumptions;
    private List<UnbilledConsumption> unbilled;

    @Data
    @AllArgsConstructor
    public static class UnbilledConsumption {
        private Long consumptionId;
        private String supplier;
        private BigDecimal usedMetrage;
    }
}
