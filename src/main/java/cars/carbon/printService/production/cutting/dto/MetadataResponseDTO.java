package cars.carbon.printService.production.cutting.dto;


import lombok.Data;
import java.util.List;

@Data
public class MetadataResponseDTO {
    private List<String> suppliers;
    private List<String> layers;
    private List<String> kitType;
    private List<String> Material;
    private List<String> TensylonTypes;
}