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
    @GeneratedValue
    private long id;
    @SequenceGenerator(
            name = "plate_history_seq",
            sequenceName = "plate_history_sequence",
            allocationSize = 1
    )

    @ManyToOne
    @JoinColumn(name = "plate_id")
    @JsonBackReference
    private Plates plate;

    @Enumerated(EnumType.STRING)
    private PlateEventType type;

    private Double value; //Metragem consumida para operação de corte

    private String details; //"Usado para OS #1234", "Ciclo 88"

    private LocalDateTime timestamp; //Gravar o horario da alteração

    private String os;
}

