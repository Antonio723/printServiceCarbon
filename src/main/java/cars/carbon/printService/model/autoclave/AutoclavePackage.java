package cars.carbon.printService.model.autoclave;

import cars.carbon.printService.enums.PackageStatus;
import cars.carbon.printService.model.plate.Plates;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="Package")
public class AutoclavePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(
            name = "package_autoclave_seq",
            sequenceName = "package_autoclave_sequence",
            allocationSize = 1
    )
    private long id;
    private String packageObservations;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id")
    @JsonBackReference
    private AutoclaveCycle autoclaveCycle;

    @OneToMany(mappedBy = "currentPackage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Plates> plates;
    private LocalDateTime creationDate;
    private LocalDateTime finishDate;
    @Enumerated (EnumType.STRING)
    private PackageStatus packageStatus;

}
