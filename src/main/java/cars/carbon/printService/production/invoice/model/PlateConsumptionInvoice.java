package cars.carbon.printService.production.invoice.model;

import cars.carbon.printService.production.cutting.model.PlateConsumption;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plate_consumption_invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlateConsumptionInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "used_metrage", nullable = false, precision = 10, scale = 2)
    private BigDecimal usedMetrage;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plate_consumption_id")
    private PlateConsumption plateConsumption;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
}