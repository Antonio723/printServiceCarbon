package cars.carbon.printService.service;

import cars.carbon.printService.dto.autoclave.*;
import cars.carbon.printService.enums.CycleStatus;
import cars.carbon.printService.enums.PlateStatus;
import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import cars.carbon.printService.repository.AutoclaveCycleRepository;
import cars.carbon.printService.repository.PlateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AutoclaveCycleService {

    @Autowired
    private AutoclaveCycleRepository autoclaveCycleRepository;
    private PlateRepository plateRepository;

    public AutoclaveCycle createCycle(AutoclaveCycleDTO dto) {
        AutoclaveCycle cycle = new AutoclaveCycle();
        cycle.setStartTime(LocalDateTime.now());
        cycle.setStatus(CycleStatus.CRIADO);
        cycle.setCicleDate(dto.getCycleDate());
        return autoclaveCycleRepository.save(cycle);
    }

    @Transactional
    public AutoclaveCycle uploadReport(Long cycleId, MultipartFile file) throws IOException {
        Optional<AutoclaveCycle> optional = autoclaveCycleRepository.findById(cycleId);
        if (optional.isEmpty()) throw new RuntimeException("Cycle not found");

        AutoclaveCycle cycle = optional.get();
        String path = "/autoclave-reports/" + file.getOriginalFilename();
        file.transferTo(new File(path));
        cycle.setReportFilePath(path);
        return autoclaveCycleRepository.save(cycle);
    }

    @Transactional
    public AutoclaveCycle updateStatus(Long cycleId, CycleStatus status) {
        AutoclaveCycle cycle = autoclaveCycleRepository.findById(cycleId)
                .orElseThrow(() -> new RuntimeException("Cycle not found"));
        switch (status){
            case CycleStatus.APPROVADO:
                if (cycle.getPackages() != null) {
                    cycle.getPackages().forEach(pkg -> {
                        if (pkg.getPlates() != null) {
                            pkg.getPlates().forEach(plate -> {
                                plate.setStatus(PlateStatus.EM_ESTOQUE);
                                plateRepository.save(plate);
                            });
                        }
                    });
                }
                break;
            default:
                cycle.setStatus(status);
        }
        return autoclaveCycleRepository.save(cycle);
    }

    @Transactional
    public AutoclaveCycle duplicateCycle(Long cycleId) {
        AutoclaveCycle original = autoclaveCycleRepository.findById(cycleId)
                .orElseThrow(() -> new RuntimeException("Cycle not found"));

        AutoclaveCycle duplicate = new AutoclaveCycle();
        duplicate.setStartTime(LocalDateTime.now());
        duplicate.setStatus(CycleStatus.DUPLICADO);
        duplicate.setReportFilePath(original.getReportFilePath());

        return autoclaveCycleRepository.save(duplicate);
    }

    @Transactional
    public List<AutoclaveCycle> getAll() {
        return autoclaveCycleRepository.findAll();
    }

    @Transactional
    public List<AutoclaveCycleWithDetailsDTO> listDetailedCycles() {
        List<AutoclaveCycle> cycles = autoclaveCycleRepository.findAll();

        return cycles.stream().map(cycle -> {
            List<AutoclavePackageDTO> packageDTOs = cycle.getPackages() != null
                    ? cycle.getPackages().stream().map(pkg -> {
                List<PlateDTO> plateDTOs = pkg.getPlates() != null
                        ? pkg.getPlates().stream().map(plate -> {
                    PlateDTO plateDTO = new PlateDTO();
                    plateDTO.setId(plate.getId());
                    plateDTO.setLayers(plate.getLayers());
                    plateDTO.setStatus(plate.getStatus());
                    plateDTO.setPlateSequence((int) plate.getPlateSequence());
                    return plateDTO;
                }).toList()
                        : List.of();

                AutoclavePackageDTO pkgDTO = new AutoclavePackageDTO();
                pkgDTO.setId(pkg.getId());
                pkgDTO.setPackageStatus(pkg.getPackageStatus());
                pkgDTO.setPlates(plateDTOs);
                pkgDTO.setTotalPlates(plateDTOs.size());
                return pkgDTO;
            }).toList()
                    : List.of();

            int totalPackages = packageDTOs.size();
            int totalPlates = packageDTOs.stream()
                    .mapToInt(p -> p.getPlates() != null ? p.getPlates().size() : 0)
                    .sum();

            Map<Long, Long> platesPerLayer = packageDTOs.stream()
                    .flatMap(pkg -> pkg.getPlates().stream())
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(
                            PlateDTO::getLayers,
                            Collectors.counting()
                    ));

            AutoclaveCycleWithDetailsDTO dto = new AutoclaveCycleWithDetailsDTO();
            dto.setId(cycle.getId());
            dto.setStartTime(cycle.getStartTime());
            dto.setStatus(cycle.getStatus().name());
            dto.setCycleObservation(cycle.getCycleObervation());
            dto.setTotalPackages(totalPackages);
            dto.setTotalPlates(totalPlates);
            dto.setPackages(packageDTOs);
            dto.setPlatesPerLayer(platesPerLayer);

            return dto;
        }).toList();
    }
}