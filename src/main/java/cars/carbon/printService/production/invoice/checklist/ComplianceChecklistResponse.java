package cars.carbon.printService.production.invoice.checklist;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ComplianceChecklistResponse {
    private Long cuttingRecordId;
    private String orderNumber;
    private boolean compliant;
    private int totalConsumptions;
    private int failedItems;
    private List<ChecklistItemResponse> items;
}
