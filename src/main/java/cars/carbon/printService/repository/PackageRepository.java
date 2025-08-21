package cars.carbon.printService.repository;

import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import cars.carbon.printService.model.autoclave.AutoclavePackage;
import cars.carbon.printService.enums.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<AutoclavePackage, Long> {
    // Busca pacotes por ciclo de autoclave
    List<AutoclavePackage> findByAutoclaveCycleId(Long cycleId);

    // Busca pacotes por status
    //List<AutoclavePackage> findByStatus(PackageStatus status);

    // Busca pacotes criados em um período
    //List<AutoclavePackage> findByCreationTimeBetween(LocalDateTime start, LocalDateTime end);

}
