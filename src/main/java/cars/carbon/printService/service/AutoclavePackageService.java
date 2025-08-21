package cars.carbon.printService.service;

import cars.carbon.printService.dto.autoclave.PackageDTO;
import cars.carbon.printService.enums.PackageStatus;
import cars.carbon.printService.enums.PlateStatus;
import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import cars.carbon.printService.model.autoclave.AutoclavePackage;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.repository.AutoclaveCycleRepository;
import cars.carbon.printService.repository.PackageRepository;
import cars.carbon.printService.repository.PlateRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AutoclavePackageService {

    private final PackageRepository packageRepository;
    private final PlateRepository plateRepository;
    private final AutoclaveCycleRepository cycleRepository;

    public AutoclavePackageService(PackageRepository packageRepository,
                                   PlateRepository plateRepository,
                                   AutoclaveCycleRepository cycleRepository) {
        this.packageRepository = packageRepository;
        this.plateRepository = plateRepository;
        this.cycleRepository = cycleRepository;
    }

    @Transactional
    public AutoclavePackage createPackage(PackageDTO dto) {
        AutoclaveCycle cycle = cycleRepository.findById(dto.getAutoclaveCycleId())
                .orElseThrow(() -> new RuntimeException("Cycle not found"));

        AutoclavePackage pkg = new AutoclavePackage();
        pkg.setPackageObservations(dto.getPackageObservations());

        List<Plates> plates = plateRepository.findAllById(dto.getPlateIds());
        plates.forEach(plate -> plate.setCurrentPackage(pkg));
        plateRepository.saveAll(plates);
        pkg.setPlates(plates);

        pkg.setAutoclaveCycle(cycle);
        pkg.setPackageStatus(PackageStatus.PREPARANDO);
        pkg.setCreationDate(LocalDateTime.now());

        return packageRepository.save(pkg);
    }

    @Transactional
    public AutoclavePackage addPlatesToPackage(Long packageId, List<Long> plateIds) {
        AutoclavePackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        List<Plates> plates = plateRepository.findAllById(plateIds);
        plates.forEach(plate -> {
            plate.setCurrentPackage(pkg);
            plate.setStatus(PlateStatus.EM_PACOTE);
            plateRepository.save(plate);
        });

        pkg.getPlates().addAll(plates);
        return packageRepository.save(pkg);
    }
    /**
     * @Transactional
    * public List<AutoclavePackage> getPackagesInCycle(Long cycleId) {
    *   return packageRepository.findByAutoclaveCycleId(cycleId);
    *}
    */

    @Transactional
    public void updatePackageStatus(Long packageId, PackageStatus status) {
        AutoclavePackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        pkg.setPackageStatus(status);
        packageRepository.save(pkg);
    }
}
