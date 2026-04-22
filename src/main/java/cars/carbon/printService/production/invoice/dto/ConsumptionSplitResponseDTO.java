package cars.carbon.printService.production.invoice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConsumptionSplitResponseDTO {
    private Long id;
    private BigDecimal usedMetrage;
    private InvoiceItemDTO invoice;
}
