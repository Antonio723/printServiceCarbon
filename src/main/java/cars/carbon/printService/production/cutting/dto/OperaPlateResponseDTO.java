package cars.carbon.printService.production.cutting.dto;


import lombok.Data;

@Data
public class OperaPlateResponseDTO {
    private Long id;
    private String batchNumber;
    private String layerQuantity;
}