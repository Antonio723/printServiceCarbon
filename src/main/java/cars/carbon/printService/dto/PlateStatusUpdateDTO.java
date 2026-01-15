package cars.carbon.printService.dto;

import cars.carbon.printService.enums.PlateStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlateStatusUpdateDTO {
    private Long plateId;
    private PlateStatus newStatus;

}
