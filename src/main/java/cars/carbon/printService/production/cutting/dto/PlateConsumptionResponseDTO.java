package cars.carbon.printService.production.cutting.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class PlateConsumptionResponseDTO {
    private Long id;
    private String invoiceNumber;
    private String batchNumber;
    private BigDecimal usedMetrage;
    private String supplier;
    private String layerQuantity;
    private Boolean manualBatch;
    private Long plateId;
    private String plateBatchNumber;
}
