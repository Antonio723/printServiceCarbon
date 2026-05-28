package cars.carbon.printService.production.invoice.checklist;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

// Migrado para o Maestro (Fase 4). Mantido sob feature flag para rollback.
@ConditionalOnProperty(name = "spring.legacy.endpoints.enabled", havingValue = "true")
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
