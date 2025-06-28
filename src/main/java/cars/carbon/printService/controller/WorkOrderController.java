package cars.carbon.printService.controller;

import cars.carbon.printService.dto.WorkOrderRequestDTO;
import cars.carbon.printService.dto.workorder.EnfestoGroupDTO;
import cars.carbon.printService.model.WorkOrders.WorkOrder;
import cars.carbon.printService.service.WorkOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workorder")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping
    public List<WorkOrder> listAll(){
        return workOrderService.listAll();
    }


    @GetMapping("/enfesto/list")
    public List<EnfestoGroupDTO> listGroupedByEnfestoDate() {
        return workOrderService.listGroupedByEnfestoDate();
    }

    @PostMapping
    public WorkOrder create(@RequestBody WorkOrderRequestDTO workOrder){
        return workOrderService.createWorkOrder(workOrder);
    }

    @DeleteMapping
    public String delete(@RequestParam Long id){
        return workOrderService.deleteAllById(id);
    }

    @PutMapping("/{id}")
    public WorkOrder update(@PathVariable Long id, @RequestBody WorkOrderRequestDTO dto) {
        return workOrderService.updateWorkOrder(id, dto);
    }
}
