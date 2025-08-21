package cars.carbon.printService.model.plate;

import cars.carbon.printService.enums.PlateStatus;
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
    @SequenceGenerator(
            name = "plate_history_seq",
            sequenceName = "plate_history_sequence",
            allocationSize = 1
    )

    @ManyToOne
    private Plates plate;

    @Enumerated(EnumType.STRING)
    private PlateStatus type;

    private Double value; //Metragem consumida para operação de corte

    private String details; //"Usado para OS #1234", "Ciclo 88"

    private LocalDateTime timestamp; //Gravar o horario da alteração

}

