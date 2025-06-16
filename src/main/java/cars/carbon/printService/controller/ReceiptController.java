package cars.carbon.printService.controller;

import cars.carbon.printService.dto.ReceiptDTO;
import cars.carbon.printService.model.receipt.Receipt;
import cars.carbon.printService.service.ReceiptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receipt")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    public List<Receipt> listAll(){
        return receiptService.listAll();
    }

    @PostMapping
    public Receipt create(@RequestBody ReceiptDTO receipt){
        return receiptService.createReceipt(receipt);
    }

    @DeleteMapping
    public String deleteById(@RequestParam Long id){
        return receiptService.deletById(id);
    }

    @PutMapping("/{id}")
    public Receipt update(@PathVariable long id, @RequestBody ReceiptDTO dto){
        return receiptService.updateReceipt(id,dto);
    }

}
