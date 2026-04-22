package cars.carbon.printService.production.invoice.repository;

import cars.carbon.printService.production.invoice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByNumber(String number);
    Optional<Invoice> findByNumberIgnoreCase(String number);
}
