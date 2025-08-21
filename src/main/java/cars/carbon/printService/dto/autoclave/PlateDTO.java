package cars.carbon.printService.dto.autoclave;

import cars.carbon.printService.enums.PlateStatus;

public class PlateDTO {
    private Long id;
    private Integer plateSequence;
    private PlateStatus status;
    private long Layers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPlateSequence() {
        return plateSequence;
    }

    public void setPlateSequence(Integer plateSequence) {
        this.plateSequence = plateSequence;
    }

    public PlateStatus getStatus() {
        return status;
    }

    public void setStatus(PlateStatus status) {
        this.status = status;
    }

    public long getLayers() {
        return Layers;
    }

    public void setLayers(long layers) {
        Layers = layers;
    }
}