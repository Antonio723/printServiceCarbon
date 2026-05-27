package cars.carbon.printService.production.quality.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Agregação usada pelo Maestro para auto-preencher o Cert. de Qualidade a
 * partir de uma NF emitida. Retorna o suficiente para o Maestro localizar
 * o(s) cert(s) de conformidade aplicáveis (via supplier + camadas) e o
 * fornecedor de tecido (via enfesto/OT que cortou as placas).
 */
public record QualitySourceResponse(
        String invoiceNumber,
        CuttingRecord cuttingRecord,
        BigDecimal totalSquareMeters,
        List<Consumption> consumptions
) {
    public record CuttingRecord(
            Long id,
            String orderNumber,
            String orderDescription,
            String material,
            String kitType
    ) {}

    public record Consumption(
            String supplier,
            String layerQuantity,
            String batchNumber,
            BigDecimal invoicedMetrage,
            Workorder workorder
    ) {}

    public record Workorder(
            Long id,
            String lote,
            String clothType,
            String clothBatch,
            String fabricSupplier
    ) {}
}
