package cars.carbon.printService.production.cutting.repository;

import cars.carbon.printService.production.cutting.model.CuttingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CuttingRecordRepository extends JpaRepository<CuttingRecord, Long> {

    @Query("""
    SELECT DISTINCT cr FROM CuttingRecord cr
    LEFT JOIN FETCH cr.consumptions c
    """)
    List<CuttingRecord> findAllWithConsumptions();
}