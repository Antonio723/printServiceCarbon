package cars.carbon.printService.repository;

import cars.carbon.printService.model.plate.Plates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlateRepository extends JpaRepository<Plates,Long> {

}
