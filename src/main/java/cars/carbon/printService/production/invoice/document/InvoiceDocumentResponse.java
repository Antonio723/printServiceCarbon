package cars.carbon.printService.production.invoice.document;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InvoiceDocumentResponse {
    private Long id;
    private String invoiceNumber;
    private DocumentType type;
    private String originalFilename;
    private Long fileSizeBytes;
    private String sha256Hash;
    private Integer version;
    private Boolean active;
    private Long replacedById;
    private String uploadedBy;
    private LocalDateTime createdAt;
}
