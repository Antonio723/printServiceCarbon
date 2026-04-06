package cars.carbon.printService.production.invoice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CuttingRecordInvoiceUpdateRequest {

    private Long cuttingRecordId;
    private Boolean singleInvoice;
    private String invoiceNumber;
    private List<ConsumptionDTO> consumptions;

    @Data
    public static class ConsumptionDTO {
        private Long id;
        private List<InvoiceDTO> invoices;
    }

    @Data
    public static class InvoiceDTO {
        private String invoiceNumber;
        private BigDecimal usedMetrage;
    }
}