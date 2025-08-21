package cars.carbon.printService.model.plate;

import cars.carbon.printService.enums.PlateStatus;
import cars.carbon.printService.model.WorkOrders.WorkOrder;
import cars.carbon.printService.model.autoclave.AutoclavePackage;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Plates")
public class Plates {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(
            name = "plate_seq",
            sequenceName = "plate_sequence",
            allocationSize = 1
    )
    private long id;

    @ManyToOne
    @JoinColumn(name = "workorderid")
    @JsonBackReference
    private WorkOrder workorderid;

    @Column(name = "plate_sequence")
    private long plateSequence;

    @OneToMany(mappedBy = "plate", cascade = CascadeType.ALL)
    private List<PlateEvent> events = new ArrayList<>();

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "package_id")
    private AutoclavePackage currentPackage;

    @Enumerated(EnumType.STRING)
    @Column()
    private PlateStatus status;

    private long Layers;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public WorkOrder getWorkorderid() {
        return workorderid;
    }

    public void setWorkorderid(WorkOrder workorderid) {
        this.workorderid = workorderid;
    }

    public long getPlateSequence() {
        return plateSequence;
    }

    public void setPlateSequence(long plateSequence) {
        this.plateSequence = plateSequence;
    }

    public List<PlateEvent> getEvents() {
        return events;
    }

    public void setEvents(List<PlateEvent> events) {
        this.events = events;
    }

    public AutoclavePackage getCurrentPackage() {
        return currentPackage;
    }

    public void setCurrentPackage(AutoclavePackage currentPackage) {
        this.currentPackage = currentPackage;
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
