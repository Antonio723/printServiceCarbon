package cars.carbon.printService.repository;

import cars.carbon.printService.model.plate.PlateEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlateEventRepository extends JpaRepository<PlateEvent, Long> {

}
