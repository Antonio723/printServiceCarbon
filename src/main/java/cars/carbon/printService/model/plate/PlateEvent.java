package cars.carbon.printService.model.plate;

import cars.carbon.printService.enums.PlateEventType;
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
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plate_id")
    @JsonBackReference
    private Plates plate;

    @Enumerated(EnumType.STRING)
    private PlateEventType eventType;

    private LocalDateTime eventDate;

    @Column(name = "consumption_reference_id")
    private Long consumptionReferenceId;

    private Double consumedArea; //Usado apenas para consumo

    private Double consumedLength; //Usado apenas para consumo

    private String description;
}