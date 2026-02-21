package cars.carbon.printService.service;

import cars.carbon.printService.dto.autoclave.*;
import cars.carbon.printService.enums.CycleStatus;
import cars.carbon.printService.enums.PackageStatus;
import cars.carbon.printService.enums.PlateStatus;
import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import cars.carbon.printService.repository.AutoclaveCycleRepository;
import cars.carbon.printService.repository.PlateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoclaveCycleService {

    private AutoclaveCycleRepository autoclaveCycleRepository;
    private PlateRepository plateRepository;

    @Autowired
    public AutoclaveCycleService(AutoclaveCycleRepository autoclaveCycleRepository,
                                 PlateRepository plateRepository) {
        this.autoclaveCycleRepository = autoclaveCycleRepository;
        this.plateRepository = plateRepository;
    }

    private final String uploadDir = System.getProperty("user.dir")
            + File.separator + "uploads"
            + File.separator + "autoclave-reports";

    public AutoclaveCycle createCycle(AutoclaveCycleDTO dto) {
        AutoclaveCycle cycle = new AutoclaveCycle();
        cycle.setCreationDate(LocalDateTime.now());
        cycle.setStatus(CycleStatus.CRIADO);
        return autoclaveCycleRepository.save(cycle);
    }

    @Transactional
    public AutoclaveCycle uploadReport(Long cycleId, MultipartFile file) throws IOException {
        AutoclaveCycle cycle = autoclaveCycleRepository.findById(cycleId)
                .orElseThrow(() -> new RuntimeException("Cycle not found"));

        // Cria diretório se não existir
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Salva o arquivo fisicamente
        String filename = file.getOriginalFilename();
        String path = uploadDir + File.separator + filename;
        File destination = new File(path);
        file.transferTo(destination);

        // Salva apenas o nome no banco
        cycle.setReportFilePath(filename);
        return autoclaveCycleRepository.save(cycle);
    }


    @Transactional
    public AutoclaveCycle completeCycleWithImage(Long cycleId, MultipartFile file, CompletCycle dto) throws IOException {
        Optional<AutoclaveCycle> optional = autoclaveCycleRepository.findById(cycleId);
        if (optional.isEmpty()) throw new RuntimeException("Cycle not found");

        AutoclaveCycle cycle = optional.get();

        // Define diretório absoluto para armazenar os arquivos
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "autoclave-reports";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs(); // cria diretórios se não existirem
        }

        // Caminho final do arquivo
        String path = uploadDir + File.separator + file.getOriginalFilename();
        File destination = new File(path);

        // Salva o arquivo fisicamente
        file.transferTo(destination);

        String fileName = file.getOriginalFilename();
        cycle.setReportFilePath(fileName);

        switch (dto.getNewStatus()){
            case CycleStatus.FINALIZADO:
                cycle.setStatus(CycleStatus.FINALIZADO);
                cycle.getPackages().forEach(pkg->{
                    pkg.setPackageStatus(PackageStatus.AGUARDANDO_APROVACAO);
                    pkg.getPlates().forEach(plate->{
                        plate.setStatus(PlateStatus.AGUARDANDO_APROVACAO);
                    });
                });
                break;
            default:
                cycle.setStatus(dto.getNewStatus());
        }
        return autoclaveCycleRepository.save(cycle);
    }


    @Transactional
    public AutoclaveCycle updateStatus(Long cycleId, ChangeStatus dto) {
        AutoclaveCycle cycle = autoclaveCycleRepository.findById(cycleId)
                .orElseThrow(() -> new RuntimeException("Cycle not found"));
        switch (dto.getNewStatus()){
            case CycleStatus.EM_ANDAMENTO:
                cycle.setStatus(CycleStatus.EM_ANDAMENTO);
                cycle.getPackages().forEach(pkg->{
                    if(pkg.getPlates() != null){
                        pkg.setPackageStatus(PackageStatus.EM_CICLO);

                        pkg.getPlates().forEach( plate->{
                            if(plate!= null){
                                plate.setStatus(PlateStatus.EM_AUTOCLAVE);
                            }
                        });
                    }else{
                        throw new RuntimeException("O ciclo não pode ser iniciado. Existem pacotes sem placas vinculadas");
                    }
                });
                break;
            case CycleStatus.FINALIZADO:
                if (cycle.getPackages() != null) {
                    cycle.getPackages().forEach(pkg -> {
                        pkg.setPackageStatus(PackageStatus.AGUARDANDO_APROVACAO);
                        if (pkg.getPlates() != null) {
                            pkg.getPlates().forEach(plate -> {
                                plate.setStatus(PlateStatus.AGUARDANDO_APROVACAO);
                                plateRepository.save(plate);
                            });
                        }
                    });
                }
                break;
            default:
                cycle.setStatus(dto.getNewStatus());
        }
        return autoclaveCycleRepository.save(cycle);
    }

    @Transactional
    public List<AutoclaveCycleWithDetailsDTO> findByByDateRange(LocalDate start, LocalDate end) {
        // Busca todos os ciclos no período
        List<AutoclaveCycle> cycles = autoclaveCycleRepository.findByCreationDateBetween(
                start.atStartOfDay(),
                end.atTime(23, 59, 59)
        );

        // Converte para DTOs detalhados usando o método existente
        return cycles.stream()
                .map(this::convertToDetailedDTO)
                .sorted((a, b) -> b.getCreationDate().compareTo(a.getCreationDate())) // Mais recente primeiro
                .toList();
    }

    private AutoclaveCycleWithDetailsDTO convertToDetailedDTO(AutoclaveCycle cycle) {
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
        dto.setCreationDate(cycle.getCreationDate());
        dto.setPackages(packageDTOs);
        dto.setPlatesPerLayer(platesPerLayer);
        dto.setReportFilePath(cycle.getReportFilePath());

        return dto;
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
    public List<AutoclaveCycleWithDetailsDTO> findByIncompleteCycles(){
        List<AutoclaveCycle> cycles = autoclaveCycleRepository.findByIncompleteCycles();
        return cycles.stream()
                .map(this::convertToDetailedDTO)
                .sorted((a, b) -> b.getCreationDate().compareTo(a.getCreationDate()))
                .toList();
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
            dto.setCreationDate(cycle.getCreationDate());
            dto.setPackages(packageDTOs);
            dto.setPlatesPerLayer(platesPerLayer);
            dto.setReportFilePath(cycle.getReportFilePath());

            return dto;
        }).toList();
    }
}