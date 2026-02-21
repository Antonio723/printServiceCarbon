package cars.carbon.printService.repository;

import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AutoclaveCycleRepository extends JpaRepository<AutoclaveCycle, Long> {
    @Query("SELECT DISTINCT c FROM AutoclaveCycle c " +
            "LEFT JOIN FETCH c.packages p " +
            "LEFT JOIN FETCH p.plates")
    List<AutoclaveCycle> findAllWithPackagesAndPlates();
    // No AutoclaveCycleRepository
    List<AutoclaveCycle> findByCreationDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT DISTINCT c
    FROM AutoclaveCycle c
    LEFT JOIN c.packages pkg
    WHERE c.status <> cars.carbon.printService.enums.CycleStatus.FINALIZADO
      AND (
            pkg IS NULL
            OR (
                pkg.packageStatus <> cars.carbon.printService.enums.PackageStatus.APROVADO
                AND NOT EXISTS (
                    SELECT p.id
                    FROM Plates p
                    WHERE p.currentPackage = pkg
                      AND p.status IN (
                            cars.carbon.printService.enums.PlateStatus.EM_ESTOQUE,
                            cars.carbon.printService.enums.PlateStatus.CONSUMO_PARCIAL,
                            cars.carbon.printService.enums.PlateStatus.CONSUMO_TOTAL
                      )
                )
            )
      )
""")
    List<AutoclaveCycle> findByIncompleteCycles();
}