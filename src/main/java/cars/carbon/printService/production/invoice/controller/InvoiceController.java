package cars.carbon.printService.production.invoice.controller;

import cars.carbon.printService.production.invoice.dto.CuttingRecordInvoiceUpdateRequest;
import cars.carbon.printService.production.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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