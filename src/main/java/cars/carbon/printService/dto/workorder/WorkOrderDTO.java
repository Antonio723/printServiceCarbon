package cars.carbon.printService.dto.workorder;

import cars.carbon.printService.model.WorkOrders.Plates;

import java.util.List;

public class WorkOrderDTO {
    private Long id;
    private String lote;
    private Long platesQuantity;
    private Long platesLayres;
    private String clothType;
    private String clothBatch;
    private String plasticType;
    private String plasticBatch;
    private String resinedBatch;
    private List<Plates> plates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Long getPlatesQuantity() {
        return platesQuantity;
    }

    public void setPlatesQuantity(Long platesQuantity) {
        this.platesQuantity = platesQuantity;
    }

    public Long getPlatesLayres() {
        return platesLayres;
    }

    public void setPlatesLayres(Long platesLayres) {
        this.platesLayres = platesLayres;
    }

    public String getClothType() {
        return clothType;
    }

    public void setClothType(String clothType) {
        this.clothType = clothType;
    }

    public String getClothBatch() {
        return clothBatch;
    }

    public void setClothBatch(String clothBatch) {
        this.clothBatch = clothBatch;
    }

    public String getPlasticType() {
        return plasticType;
    }

    public void setPlasticType(String plasticType) {
        this.plasticType = plasticType;
    }

    public String getPlasticBatch() {
        return plasticBatch;
    }

    public void setPlasticBatch(String plasticBatch) {
        this.plasticBatch = plasticBatch;
    }

    public String getResinedBatch() {
        return resinedBatch;
    }

    public void setResinedBatch(String resinedBatch) {
        this.resinedBatch = resinedBatch;
    }

    public List<Plates> getPlates() {
        return plates;
    }

    public void setPlates(List<Plates> plates) {
        this.plates = plates;
    }
}
