package cars.carbon.printService.production.invoice.repository;

import cars.carbon.printService.production.invoice.model.PlateConsumptionInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlateConsumptionInvoiceRepository
        extends JpaRepository<PlateConsumptionInvoice, Long> {

    @Query("""
    SELECT ci FROM PlateConsumptionInvoice ci
    JOIN FETCH ci.invoice
    WHERE ci.plateConsumption.id IN :ids
    """)
    List<PlateConsumptionInvoice> findByConsumptionIds(List<Long> ids);
}