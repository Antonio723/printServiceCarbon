package cars.carbon.printService.dto;

import cars.carbon.printService.enums.PlateStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlateStatusUpdateDTO {
    private Long plateId;
    private PlateStatus newStatus;

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
    }

    public PlateStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(PlateStatus newStatus) {
        this.newStatus = newStatus;
    }
}
