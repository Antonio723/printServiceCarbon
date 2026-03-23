package cars.carbon.printService.production.cutting.dto;


import cars.carbon.printService.production.cutting.enums.KitType;
import cars.carbon.printService.production.cutting.enums.MaterialType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CuttingRecordResponseDTO {
    private Long id;
    private LocalDateTime productionDate;
    private String orderNumber;
    private String orderDescription;
    private LocalDateTime createdAt;
    private List<PlateConsumptionResponseDTO> consumptions;
    private MaterialType material;
    private KitType kitType;
}
