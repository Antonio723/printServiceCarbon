package cars.carbon.printService.model.plate;

import cars.carbon.printService.enums.PlateStatus;
import cars.carbon.printService.model.WorkOrders.WorkOrder;
import cars.carbon.printService.model.autoclave.AutoclavePackage;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
    @JsonManagedReference
    private List<PlateEvent> events = new ArrayList<>();

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "package_id")
    private AutoclavePackage currentPackage;

    @Enumerated(EnumType.STRING)
    @Column()
    private PlateStatus status;

    private long Layers;

    private double actualSize;
    private double initSize;

    @JsonProperty("packageId")
    public Long getPackageId() {
        return currentPackage != null ? currentPackage.getId() : null;
    }

}
