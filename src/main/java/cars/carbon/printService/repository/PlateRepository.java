package cars.carbon.printService.repository;

import cars.carbon.printService.enums.PlateStatus;
import cars.carbon.printService.model.plate.Plates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlateRepository extends JpaRepository<Plates,Long> {
    List<Plates> findByStatusIn(List<PlateStatus> statuses);
}
