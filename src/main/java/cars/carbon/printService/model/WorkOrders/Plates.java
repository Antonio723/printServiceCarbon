package cars.carbon.printService.model.WorkOrders;

import cars.carbon.printService.model.WorkOrders.WorkOrder;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name="Plates")
public class Plates {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(
            name = "plate_seq",
            sequenceName = "plate_sequence",
            allocationSize = 1,
            initialValue = 999
    )
    private long id;

    @ManyToOne
    @JoinColumn(name = "workorderid")
    @JsonBackReference
    private WorkOrder workorderid;

    @Column(name = "plate_sequence")
    private long plateSequence;

    @Deprecated
    public Plates() {}

    public long getId() {
        return id;
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
}
