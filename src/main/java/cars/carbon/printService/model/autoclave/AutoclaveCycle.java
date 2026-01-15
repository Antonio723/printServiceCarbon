package cars.carbon.printService.model.autoclave;

import cars.carbon.printService.enums.CycleStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class AutoclaveCycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime creationDate;
    private LocalDateTime startTime;
    private LocalDateTime cicleDate;

    private CycleStatus status;

    private String reportFilePath;
    private String cycleObervation;
    @OneToMany(mappedBy = "autoclaveCycle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AutoclavePackage> packages;

}