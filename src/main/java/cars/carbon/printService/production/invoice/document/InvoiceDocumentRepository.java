package cars.carbon.printService.production.invoice.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceDocumentRepository extends JpaRepository<InvoiceDocument, Long> {

    List<InvoiceDocument> findByInvoice_NumberAndActiveTrue(String number);

    List<InvoiceDocument> findByInvoice_NumberOrderByVersionDesc(String number);

    Optional<InvoiceDocument> findByInvoice_NumberAndTypeAndActiveTrue(String number, DocumentType type);

    List<InvoiceDocument> findByActiveTrue();

    @Query("""
        SELECT d FROM InvoiceDocument d
        WHERE d.invoice.number IN :numbers
          AND d.type = :type
          AND d.active = true
        """)
    List<InvoiceDocument> findActiveByInvoiceNumbersAndType(List<String> numbers, DocumentType type);
}
