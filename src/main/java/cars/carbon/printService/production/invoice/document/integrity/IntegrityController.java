package cars.carbon.printService.production.invoice.document.integrity;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Migrado para o Maestro (Fase 4). Mantido sob feature flag para rollback.
@ConditionalOnProperty(name = "spring.legacy.endpoints.enabled", havingValue = "true")
@RestController
@RequestMapping("/invoices/integrity")
@RequiredArgsConstructor
public class IntegrityController {

    private final DocumentIntegrityJob integrityJob;
    private final IntegrityCheckResultRepository checkRepo;

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runManually() {
        List<IntegrityCheckResult> results = integrityJob.run();
        long ok       = results.stream().filter(r -> r.getStatus() == IntegrityStatus.OK).count();
        long corrupted = results.stream().filter(r -> r.getStatus() == IntegrityStatus.CORRUPTED).count();
        long missing  = results.stream().filter(r -> r.getStatus() == IntegrityStatus.MISSING).count();

        return ResponseEntity.ok(Map.of(
                "total",     results.size(),
                "ok",        ok,
                "corrupted", corrupted,
                "missing",   missing
        ));
    }

    @GetMapping("/failures")
    public List<IntegrityCheckResult> getLatestFailures() {
        return checkRepo.findLatestFailures();
    }

    @GetMapping("/document/{documentId}")
    public List<IntegrityCheckResult> getHistoryForDocument(@PathVariable Long documentId) {
        return checkRepo.findByDocument_IdOrderByCheckedAtDesc(documentId);
    }
}
