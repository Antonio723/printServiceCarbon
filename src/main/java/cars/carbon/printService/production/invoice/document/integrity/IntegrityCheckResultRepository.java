package cars.carbon.printService.production.invoice.document.integrity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IntegrityCheckResultRepository extends JpaRepository<IntegrityCheckResult, Long> {

    List<IntegrityCheckResult> findByDocument_IdOrderByCheckedAtDesc(Long documentId);

    List<IntegrityCheckResult> findByStatusOrderByCheckedAtDesc(IntegrityStatus status);

    @Query("""
        SELECT r FROM IntegrityCheckResult r
        WHERE r.checkedAt = (
            SELECT MAX(r2.checkedAt) FROM IntegrityCheckResult r2
            WHERE r2.document.id = r.document.id
        )
        AND r.status <> 'OK'
        ORDER BY r.checkedAt DESC
        """)
    List<IntegrityCheckResult> findLatestFailures();
}
