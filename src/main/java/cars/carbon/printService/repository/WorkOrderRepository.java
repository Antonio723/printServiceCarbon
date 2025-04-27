package cars.carbon.printService.repository;

import cars.carbon.printService.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

}
