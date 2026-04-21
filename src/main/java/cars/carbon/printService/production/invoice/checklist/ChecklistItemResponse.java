package cars.carbon.printService.production.invoice.checklist;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChecklistItemResponse {
    private Long consumptionId;
    private String supplier;
    private ChecklistRule rule;
    private String description;
    private boolean passed;
}
