package cars.carbon.printService.model.plate;

import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import cars.carbon.printService.model.autoclave.AutoclavePackage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Plates plate;

    private String status;

    private LocalDateTime timestamp;

    private String description;

    @ManyToOne
    private AutoclaveCycle cycle;

    @ManyToOne
    private AutoclavePackage packageEntity;
}
