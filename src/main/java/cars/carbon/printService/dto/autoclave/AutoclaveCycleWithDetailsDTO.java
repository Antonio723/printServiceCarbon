package cars.carbon.printService.dto.autoclave;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AutoclaveCycleWithDetailsDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime creationDate;
    private String status;
    private String cycleObservation;
    private String reportFilePath;

    private int totalPackages;
    private int totalPlates;

    private List<AutoclavePackageDTO> packages;
    private Map<Long, Long> platesPerLayer;
}
