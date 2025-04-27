package cars.carbon.printService.model.WorkOrders;

import cars.carbon.printService.model.WorkOrder;
import jakarta.persistence.*;

@Entity
@Table(name="Plates")
public class Plates {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    @JoinColumn(name = "workorder_id")
    private WorkOrder workorderid;
    private long plate_sequence;

    @Deprecated
    public Plates() {
    }

    public long getId() {
        return id;
    }

    public WorkOrder getWorkorderid() {
        return workorderid;
    }

    public void setWorkorderid(WorkOrder workorderid) {
        this.workorderid = workorderid;
    }

    public long getPlate_sequence() {
        return plate_sequence;
    }

    public void setPlate_sequence(long plate_sequence) {
        this.plate_sequence = plate_sequence;
    }
}

