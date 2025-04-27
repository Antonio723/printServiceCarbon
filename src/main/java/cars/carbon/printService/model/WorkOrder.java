package cars.carbon.printService.model;

import cars.carbon.printService.model.WorkOrders.Plates;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Workorder_table")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime creationDate;
    private LocalDateTime changeDate;
    private String lote;
    private Long platesQuantity;
    @OneToMany(mappedBy = "workorderid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plates> plates = new ArrayList<>();
    private String enfestoType;//must be a relation in database with products entity
    private String filmeTye;//must be a relation in database with products entity
    private Long platesLayres;

    @Deprecated
    public WorkOrder() {}

    public WorkOrder(String enfestoType, Long id, LocalDateTime creationDate, LocalDateTime changeDate, String lote, Long platesQuantity, String filmeTye, Long platesLayres) {
        this.enfestoType = enfestoType;
        this.id = id;
        this.creationDate = creationDate;
        this.changeDate = changeDate;
        this.lote = lote;
        this.platesQuantity = platesQuantity;
        this.filmeTye = filmeTye;
        this.platesLayres = platesLayres;
    }

    public String getFilmeTye() {
        return filmeTye;
    }

    public void setFilmeTye(String filmeTye) {
        this.filmeTye = filmeTye;
    }

    public String getEnfestoType() {
        return enfestoType;
    }

    public void setEnfestoType(String enfestoType) {
        this.enfestoType = enfestoType;
    }

    public Long getPlatesQuantity() {
        return platesQuantity;
    }

    public void setPlatesQuantity(Long platesQuantity) {
        this.platesQuantity = platesQuantity;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public Long getPlatesLayres() {
        return platesLayres;
    }

    public void setPlatesLayres(Long platesLayres) {
        this.platesLayres = platesLayres;
    }

    public void setPlates(List<Plates> plates) {
        this.plates.clear();
        this.plates.addAll(plates);
    }
}
