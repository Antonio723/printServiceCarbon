package cars.carbon.printService.model.plate;

import cars.carbon.printService.enums.PlateEventType;
import cars.carbon.printService.enums.PlateStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PlateEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plate_history_seq")
    @SequenceGenerator(
            name = "plate_history_seq",
            sequenceName = "plate_history_sequence",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plate_id", nullable = false)
    @JsonBackReference
    private Plates plate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlateEventType type;

    @Column(nullable = false)
    private LocalDateTime timestamp;
    private String details;
}

