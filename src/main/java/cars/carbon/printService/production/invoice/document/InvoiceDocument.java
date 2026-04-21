package cars.carbon.printService.production.invoice.document;

import cars.carbon.printService.production.invoice.model.Invoice;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "sha256_hash", nullable = false)
    private String sha256Hash;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private Boolean active;

    // aponta para o documento que substituiu este (null se ainda é o ativo)
    @ManyToOne
    @JoinColumn(name = "replaced_by_id")
    private InvoiceDocument replacedBy;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
