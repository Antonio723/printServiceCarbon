package cars.carbon.printService.production.invoice.aging;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

// Migrado para o Maestro (Fase 4). Mantido sob feature flag para rollback.
@ConditionalOnProperty(name = "spring.legacy.endpoints.enabled", havingValue = "true")
@RestController
@RequestMapping("/cutting-records/invoices/aging")
@RequiredArgsConstructor
public class InvoiceAgingController {

    private final InvoiceAgingService service;

    @GetMapping
    public AgingReportResponse report() {
        return service.generate();
    }
}
