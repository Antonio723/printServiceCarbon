package cars.carbon.printService.repository;

import cars.carbon.printService.model.WorkOrders.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByEnfestoDateBetween(LocalDate start, LocalDate end);
}
