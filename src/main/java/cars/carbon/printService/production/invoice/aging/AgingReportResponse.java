package cars.carbon.printService.production.invoice.aging;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class AgingReportResponse {
    private LocalDateTime generatedAt;
    private Map<AgingBucket, Long> summary;
    private List<AgingItemResponse> items;
}
