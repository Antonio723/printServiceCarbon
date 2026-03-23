package cars.carbon.printService.production.cutting.model;

import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.production.cutting.enums.SupplierType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "plate_consumptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlateConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @Column(name = "used_metrage", nullable = false, precision = 10, scale = 2)
    private BigDecimal usedMetrage;

    @Enumerated(EnumType.STRING)
    @Column(name = "supplier", nullable = false)
    private SupplierType supplier;

    @Column(name = "layer_quantity", nullable = false)
    private String layerQuantity;

    @Column(name = "manual_batch", nullable = false)
    private Boolean manualBatch;

    @ManyToOne
    @JoinColumn(name = "plate_id")
    private Plates plate;

    @ManyToOne
    @JoinColumn(name = "cutting_record_id", nullable = false)
    private CuttingRecord cuttingRecord;
}
