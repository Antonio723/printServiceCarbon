package cars.carbon.printService.production.cutting.dto;


import cars.carbon.printService.production.cutting.enums.MaterialType;
import cars.carbon.printService.production.cutting.enums.SupplierType;
import cars.carbon.printService.production.invoice.dto.InvoiceItemDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PlateConsumptionRequestDTO {

    private String invoiceNumber;
    private String batchNumber;
    private BigDecimal usedMetrage;
    private SupplierType supplier;
    private String layerQuantity;
    private Long plateId;
    private MaterialType material;

    private List<InvoiceItemDTO> invoices;
}