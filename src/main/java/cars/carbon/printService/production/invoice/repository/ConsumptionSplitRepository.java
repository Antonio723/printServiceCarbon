package cars.carbon.printService.production.invoice.repository;

import cars.carbon.printService.production.invoice.model.ConsumptionSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConsumptionSplitRepository extends JpaRepository<ConsumptionSplit, Long> {

    @Query("""
        SELECT cs FROM ConsumptionSplit cs
        JOIN FETCH cs.invoice
        WHERE cs.plateConsumption.id IN :ids
        """)
    List<ConsumptionSplit> findByConsumptionIds(List<Long> ids);
}
