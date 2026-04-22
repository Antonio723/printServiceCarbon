package cars.carbon.printService.production.invoice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String number;

    @Column(name = "nf_file_path")
    private String nfFilePath;

    @Column(name = "correction_file_path")
    private String correctionFilePath;
}