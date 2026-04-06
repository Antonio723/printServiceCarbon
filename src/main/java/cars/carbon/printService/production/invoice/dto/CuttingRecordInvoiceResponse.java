package cars.carbon.printService.production.invoice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CuttingRecordInvoiceResponse {

    private Long cuttingRecordId;
    private Boolean singleInvoice;
    private List<ConsumptionResponse> consumptions;

    @Data
    public static class ConsumptionResponse {
        private Long id;
        private BigDecimal usedMetrage;
        private List<InvoiceResponse> invoices;
    }

    @Data
    public static class InvoiceResponse {
        private String invoiceNumber;
        private BigDecimal usedMetrage;
    }
}