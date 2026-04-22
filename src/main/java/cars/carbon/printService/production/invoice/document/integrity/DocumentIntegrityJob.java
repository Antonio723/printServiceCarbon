package cars.carbon.printService.production.invoice.document.integrity;

import cars.carbon.printService.production.invoice.document.InvoiceDocument;
import cars.carbon.printService.production.invoice.document.InvoiceDocumentRepository;
import cars.carbon.printService.production.invoice.document.InvoiceDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentIntegrityJob {

    private final InvoiceDocumentRepository documentRepo;
    private final IntegrityCheckResultRepository checkRepo;
    private final InvoiceDocumentService documentService;

    // Executa toda segunda-feira às 02:00 por padrão; sobrescrevível via invoice.integrity.cron
    @Scheduled(cron = "${invoice.integrity.cron:0 0 2 * * MON}")
    @Transactional
    public List<IntegrityCheckResult> run() {
        List<InvoiceDocument> documents = documentRepo.findByActiveTrue();
        List<IntegrityCheckResult> results = new ArrayList<>();

        log.info("[IntegrityJob] Iniciando verificação de {} documento(s)", documents.size());

        for (InvoiceDocument doc : documents) {
            IntegrityCheckResult result = verify(doc);
            results.add(checkRepo.save(result));

            if (result.getStatus() != IntegrityStatus.OK) {
                log.warn("[IntegrityJob] FALHA — doc.id={} nf={} status={} notes={}",
                        doc.getId(),
                        doc.getInvoice().getNumber(),
                        result.getStatus(),
                        result.getNotes());
            }
        }

        long failures = results.stream().filter(r -> r.getStatus() != IntegrityStatus.OK).count();
        log.info("[IntegrityJob] Concluído: {}/{} com falha", failures, results.size());

        return results;
    }

    private IntegrityCheckResult verify(InvoiceDocument doc) {
        IntegrityCheckResult result = new IntegrityCheckResult();
        result.setDocument(doc);
        result.setStoredHash(doc.getSha256Hash());
        result.setCheckedAt(LocalDateTime.now());

        Path path = Paths.get(doc.getStoragePath());

        if (!Files.exists(path)) {
            result.setStatus(IntegrityStatus.MISSING);
            result.setNotes("Arquivo não encontrado em: " + doc.getStoragePath());
            return result;
        }

        try {
            String computed = documentService.computeSha256(path);
            result.setComputedHash(computed);

            if (computed.equals(doc.getSha256Hash())) {
                result.setStatus(IntegrityStatus.OK);
            } else {
                result.setStatus(IntegrityStatus.CORRUPTED);
                result.setNotes("Hash divergente. Esperado: " + doc.getSha256Hash()
                        + " | Calculado: " + computed);
            }
        } catch (Exception e) {
            result.setStatus(IntegrityStatus.CORRUPTED);
            result.setNotes("Erro ao calcular hash: " + e.getMessage());
        }

        return result;
    }
}
