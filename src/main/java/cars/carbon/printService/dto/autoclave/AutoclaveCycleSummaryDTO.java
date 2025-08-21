package cars.carbon.printService.dto.autoclave;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AutoclaveCycleSummaryDTO {
    private Long cycleId;
    private LocalDateTime startTime;
    private int totalPackages;
    private int totalPlates;

    public AutoclaveCycleSummaryDTO(Long cycleId, LocalDateTime startTime, int totalPackages, int totalPlates) {
        this.cycleId = cycleId;
        this.startTime = startTime;
        this.totalPackages = totalPackages;
        this.totalPlates = totalPlates;
    }
}
