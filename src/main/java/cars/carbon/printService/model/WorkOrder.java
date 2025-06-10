package cars.carbon.printService.model;

import cars.carbon.printService.model.WorkOrders.Plates;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "Workorder_table")
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "workorder_seq",
            sequenceName = "workorder_sequence",
            initialValue = 250,
            allocationSize = 1

    )
    private Long id;

    private LocalDateTime creationDate;
    private LocalDateTime changeDate;
    private String lote;
    private Long platesQuantity;

    @OneToMany(mappedBy = "workorderid", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Plates> plates = new ArrayList<>();

    private String clothType;
    private String clothBatch;
    private String plasticType;
    private String plasticBatch;
    private Long platesLayres;
    private String resinedBatch;
    private Date enfestoDate;

    @Deprecated
    public WorkOrder() {}

    public WorkOrder(String clothType, Long id, LocalDateTime creationDate, LocalDateTime changeDate, String lote, Long platesQuantity, String plasticType, Long platesLayres) {
        this.clothType = clothType;
        this.id = id;
        this.creationDate = creationDate;
        this.changeDate = changeDate;
        this.lote = lote;
        this.platesQuantity = platesQuantity;
        this.plasticType = plasticType;
        this.platesLayres = platesLayres;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
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

    public Long getPlatesQuantity() {
        return platesQuantity;
    }

    public void setPlatesQuantity(Long platesQuantity) {
        this.platesQuantity = platesQuantity;
    }

    public List<Plates> getPlates() {
        return plates;
    }

    public void setPlates(List<Plates> plates) {
        this.plates.clear();
        if (plates != null) {
            this.plates.addAll(plates);
        }
    }

    public String getClothType() {
        return clothType;
    }

    public void setClothType(String clothType) {
        this.clothType = clothType;
    }

    public String getPlasticType() {
        return plasticType;
    }

    public void setPlasticType(String plasticType) {
        this.plasticType = plasticType;
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

    public Date getEnfestoDate() {
        return enfestoDate;
    }

    public void setEnfestoDate(Date enfestoDate) {
        this.enfestoDate = enfestoDate;
    }
}
