package cars.carbon.printService.production.invoice.controller;

import cars.carbon.printService.production.invoice.dto.CuttingRecordInvoiceUpdateRequest;
import cars.carbon.printService.production.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

// Migrado para o Maestro (Fase 4). Mantido sob feature flag para rollback.
@ConditionalOnProperty(name = "spring.legacy.endpoints.enabled", havingValue = "true")
@RestController
@RequestMapping("/cutting-records/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService service;

    @PutMapping
    public void update(@RequestBody CuttingRecordInvoiceUpdateRequest request) {
        service.update(request);
    }
}
