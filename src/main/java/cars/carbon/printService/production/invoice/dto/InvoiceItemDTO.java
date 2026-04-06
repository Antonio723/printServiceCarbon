package cars.carbon.printService.production.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemDTO {
    private String invoiceNumber;
    private BigDecimal usedMetrage;
}