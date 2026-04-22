package cars.carbon.printService.production.invoice.model;

import cars.carbon.printService.production.cutting.model.PlateConsumption;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "consumption_splits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "used_metrage", nullable = false, precision = 10, scale = 2)
    private BigDecimal usedMetrage;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plate_consumption_id", nullable = false)
    private PlateConsumption plateConsumption;
}
