package cars.carbon.printService.production.cutting.model;


import cars.carbon.printService.production.cutting.enums.KitType;
import cars.carbon.printService.production.cutting.enums.MaterialType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cutting_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuttingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "production_date", nullable = false)
    private LocalDateTime productionDate;

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @Column(name = "order_description", nullable = false)
    private String orderDescription;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "material")
    private MaterialType material;

    @Enumerated(EnumType.STRING)
    @Column(name = "kit_type")
    private KitType kitType;

    @Column(name = "seal", nullable = true)
    private String seal;

    @OneToMany(mappedBy = "cuttingRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlateConsumption> consumptions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (productionDate == null) {
            productionDate = LocalDateTime.now();
        }
    }
}
