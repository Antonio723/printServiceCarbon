package cars.carbon.printService.production.cutting.dto;


import cars.carbon.printService.production.cutting.enums.MaterialType;
import cars.carbon.printService.production.cutting.enums.SupplierType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PlateConsumptionRequestDTO {

    private String invoiceNumber;
    private String batchNumber;
    private BigDecimal usedMetrage;
    private SupplierType supplier;
    private String layerQuantity;
    private Long plateId;
    private MaterialType material;
}