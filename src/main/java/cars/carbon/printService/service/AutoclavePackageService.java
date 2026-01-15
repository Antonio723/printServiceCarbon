package cars.carbon.printService.service;

import cars.carbon.printService.dto.autoclave.PackageDTO;
import cars.carbon.printService.dto.autoclave.packing.AutoclaveStatusChange;
import cars.carbon.printService.dto.autoclave.packing.RemovePlateDTO;
import cars.carbon.printService.enums.PackageStatus;
import cars.carbon.printService.enums.PlateEventType;
import cars.carbon.printService.enums.PlateStatus;
import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import cars.carbon.printService.model.autoclave.AutoclavePackage;
import cars.carbon.printService.model.plate.PlateEvent;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.repository.AutoclaveCycleRepository;
import cars.carbon.printService.repository.PackageRepository;
import cars.carbon.printService.repository.PlateEventRepository;
import cars.carbon.printService.repository.PlateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class AutoclavePackageService {

    private final PackageRepository packageRepository;
    private final PlateRepository plateRepository;
    private final AutoclaveCycleRepository cycleRepository;
    @Autowired
    private PlateEventRepository plateEventRepository;

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
            PlateEvent event = new PlateEvent();
            event.setPlate(plate);
            event.setDetails(String.format("Placa %d adicionada ao pacote %d", plate.getId(), pkg.getId()));
            event.setTimestamp(LocalDateTime.now());
            event.setType(PlateEventType.AUTOCLAVE);
            plateEventRepository.save(event);
            plateRepository.save(plate);
        });

        pkg.getPlates().addAll(plates);
        return packageRepository.save(pkg);
    }


    @Transactional
    public AutoclavePackage updatePackageStatus(Long packageId, AutoclaveStatusChange dto) {
        AutoclavePackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Pacote não encontrado"));
        switch (PackageStatus.valueOf(dto.getNewStatus())) {
            case APROVADO:
                pkg.setPackageStatus(PackageStatus.APROVADO);
                pkg.setFinishDate(LocalDateTime.now());
                pkg.getPlates().removeIf(plate -> {
                    switch (plate.getStatus()) {
                        case REPASSE:
                            plate.setCurrentPackage(null);
                            PlateEvent repasseEvent = new PlateEvent();
                            repasseEvent.setPlate(plate);
                            repasseEvent.setType(PlateEventType.AUTOCLAVE);
                            repasseEvent.setTimestamp(LocalDateTime.now());
                            repasseEvent.setDetails("Placa removida do pacote " + pkg.getId() + " por "+pkg.getPackageStatus());

                            plateEventRepository.save(repasseEvent);
                            plateRepository.save(plate);
                            return true;
                        default:
                            plate.setStatus(PlateStatus.EM_ESTOQUE);
                            plateRepository.save(plate);
                            return false;
                    }
                });
                break;

            case FALHOU:
                pkg.setPackageStatus(PackageStatus.FALHOU);
                pkg.getPlates().forEach(plate -> {
                    plate.setStatus(PlateStatus.REPASSE);
                    plateRepository.save(plate);
                });
                break;

            default:
                throw new RuntimeException("Status não reconhecido");
        }
        return packageRepository.save(pkg);
    }


    @Transactional
    public AutoclavePackage removePlateFromPackage(RemovePlateDTO dto) {
        AutoclavePackage pkg = packageRepository.findById(dto.getPackid())
                .orElseThrow(() -> new RuntimeException("Pacote não encontrado"));

        Plates plate = plateRepository.findById(dto.getPlateId())
                .orElseThrow(() -> new RuntimeException("Placa não encontrada"));

        // Verifica se a placa realmente pertence ao pacote informado
        if (!pkg.getPlates().contains(plate)) {
            throw new RuntimeException("A placa não pertence a este pacote");
        }

        // Desvincula a placa do pacote
        plate.setCurrentPackage(null);
        plate.setStatus(PlateStatus.EM_ENFESTO); // ou outro status adequado
        plateRepository.save(plate);

        // Remove da lista do pacote
        pkg.getPlates().remove(plate);

        // Atualiza o pacote
        return packageRepository.save(pkg);
    }

}
