package cars.carbon.printService.repository;

import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutoclaveCycleRepository extends JpaRepository<AutoclaveCycle, Long> {
    @Query("SELECT DISTINCT c FROM AutoclaveCycle c " +
            "LEFT JOIN FETCH c.packages p " +
            "LEFT JOIN FETCH p.plates")
    List<AutoclaveCycle> findAllWithPackagesAndPlates();
}