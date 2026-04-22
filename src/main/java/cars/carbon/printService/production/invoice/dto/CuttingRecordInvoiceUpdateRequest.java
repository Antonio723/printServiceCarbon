package cars.carbon.printService.production.invoice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CuttingRecordInvoiceUpdateRequest {

    private Long cuttingRecordId;
    private Boolean singleInvoice;
    private List<ConsumptionDTO> consumptions;

    @Data
    public static class ConsumptionDTO {
        private Long id;
        private List<InvoiceDTO> invoices;
        private List<SplitDTO> splits;
    }

    @Data
    public static class InvoiceDTO {
        private String number;
        private BigDecimal usedMetrage;
    }

    @Data
    public static class SplitDTO {
        private BigDecimal usedMetrage;
        private InvoiceRefDTO invoice;
    }

    @Data
    public static class InvoiceRefDTO {
        private String number;
    }
}
