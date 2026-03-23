package cars.carbon.printService.production.cutting.repository;

import cars.carbon.printService.production.cutting.model.CuttingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuttingRecordRepository extends JpaRepository<CuttingRecord, Long> {
}
