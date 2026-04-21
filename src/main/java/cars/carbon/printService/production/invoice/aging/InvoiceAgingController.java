package cars.carbon.printService.production.invoice.aging;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cutting-records/invoices/aging")
@RequiredArgsConstructor
public class InvoiceAgingController {

    private final InvoiceAgingService service;

    @GetMapping
    public AgingReportResponse report() {
        return service.generate();
    }
}
