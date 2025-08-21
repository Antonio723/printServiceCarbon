package cars.carbon.printService.repository;

import cars.carbon.printService.model.plate.PlateHistory;
import cars.carbon.printService.model.plate.Plates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlateHistoryRepository extends JpaRepository<PlateHistory, Long> {
    List<PlateHistory> findByPlateOrderByTimestampAsc(Plates plate);
}