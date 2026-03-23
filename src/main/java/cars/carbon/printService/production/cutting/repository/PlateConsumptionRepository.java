package cars.carbon.printService.production.cutting.repository;

import cars.carbon.printService.production.cutting.model.PlateConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateConsumptionRepository extends JpaRepository<PlateConsumption, Long> {
}
