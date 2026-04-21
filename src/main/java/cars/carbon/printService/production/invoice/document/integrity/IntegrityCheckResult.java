package cars.carbon.printService.production.invoice.document.integrity;

import cars.carbon.printService.production.invoice.document.InvoiceDocument;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_integrity_checks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrityCheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "document_id")
    private InvoiceDocument document;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrityStatus status;

    @Column(name = "stored_hash", nullable = false)
    private String storedHash;

    @Column(name = "computed_hash")
    private String computedHash;

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;

    @Column
    private String notes;
}
