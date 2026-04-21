package cars.carbon.printService.production.invoice.checklist;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cutting-records/{id}/compliance-checklist")
@RequiredArgsConstructor
public class ComplianceChecklistController {

    private final ComplianceChecklistService service;

    @GetMapping
    public ComplianceChecklistResponse evaluate(@PathVariable Long id) {
        return service.evaluate(id);
    }
}
