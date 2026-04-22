package cars.carbon.printService.production.cutting.model;

import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.production.cutting.enums.SupplierType;
import cars.carbon.printService.production.invoice.model.ConsumptionSplit;
import cars.carbon.printService.production.invoice.model.PlateConsumptionInvoice;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plate_consumptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlateConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToMany(mappedBy = "plateConsumption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlateConsumptionInvoice> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "plateConsumption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumptionSplit> splits = new ArrayList<>();

    @Column(name = "used_metrage", nullable = false, precision = 10, scale = 2)
    private BigDecimal usedMetrage;

    @Column(name = "batch_number")
    private String batchNumber;

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
