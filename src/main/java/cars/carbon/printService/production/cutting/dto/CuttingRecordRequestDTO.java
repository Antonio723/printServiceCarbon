package cars.carbon.printService.production.cutting.dto;


import cars.carbon.printService.production.cutting.enums.KitType;
import cars.carbon.printService.production.cutting.enums.MaterialType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CuttingRecordRequestDTO {

    private LocalDateTime productionDate;

    private String orderNumber;

    private String orderDescription;

    private MaterialType material;
    private KitType kitType;

    private List<PlateConsumptionRequestDTO> consumptions;
}
