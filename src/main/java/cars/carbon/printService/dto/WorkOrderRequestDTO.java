package cars.carbon.printService.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class WorkOrderRequestDTO {
    private String clothType;
    private String clothBatch;
    private String plasticType;
    private String plasticBatch;
    private String lote;
    private Long platesQuantity;
    private Long platesLayres;
    private String resinedBatch;
    private LocalDate enfestoDate;

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
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

    public String getPlasticBatch() {
        return plasticBatch;
    }

    public void setPlasticBatch(String plasticBatch) {
        this.plasticBatch = plasticBatch;
    }

    public String getPlasticType() {
        return plasticType;
    }

    public void setPlasticType(String plasticType) {
        this.plasticType = plasticType;
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

    public String getResinedBatch() {
        return resinedBatch;
    }

    public void setResinedBatch(String resinedBatch) {
        this.resinedBatch = resinedBatch;
    }

    public LocalDate  getEnfestoDate() {
        return enfestoDate;
    }

    public void setEnfestoDate(LocalDate enfestoDate) {
        this.enfestoDate = enfestoDate;
    }
}
