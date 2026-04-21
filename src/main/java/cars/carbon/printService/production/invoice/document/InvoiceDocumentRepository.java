package cars.carbon.printService.production.invoice.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceDocumentRepository extends JpaRepository<InvoiceDocument, Long> {

    List<InvoiceDocument> findByInvoice_InvoiceNumberAndActiveTrue(String invoiceNumber);

    List<InvoiceDocument> findByInvoice_InvoiceNumberOrderByVersionDesc(String invoiceNumber);

    Optional<InvoiceDocument> findByInvoice_InvoiceNumberAndTypeAndActiveTrue(
            String invoiceNumber, DocumentType type);

    List<InvoiceDocument> findByActiveTrue();

    @Query("""
        SELECT d FROM InvoiceDocument d
        WHERE d.invoice.invoiceNumber IN :invoiceNumbers
          AND d.type = :type
          AND d.active = true
        """)
    List<InvoiceDocument> findActiveByInvoiceNumbersAndType(
            List<String> invoiceNumbers, DocumentType type);
}
