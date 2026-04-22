package cars.carbon.printService.production.cutting.dto;

import cars.carbon.printService.production.invoice.dto.ConsumptionSplitResponseDTO;
import cars.carbon.printService.production.invoice.dto.InvoiceItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlateConsumptionResponseDTO {
    private Long id;
    private String batchNumber;
    private BigDecimal usedMetrage;
    private String supplier;
    private String layerQuantity;
    private Boolean manualBatch;
    private Long plateId;
    private String plateBatchNumber;

    private List<InvoiceItemDTO> invoices;
    private List<ConsumptionSplitResponseDTO> splits;
}
