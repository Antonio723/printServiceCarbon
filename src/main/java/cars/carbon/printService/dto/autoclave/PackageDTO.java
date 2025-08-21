package cars.carbon.printService.dto.autoclave;

import cars.carbon.printService.enums.PackageStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PackageDTO {
    private String packageObservations;
    private Long autoclaveCycleId;
    private List<Long> plateIds;
    private LocalDateTime creationTime;
    private PackageStatus packageStatus;
}
