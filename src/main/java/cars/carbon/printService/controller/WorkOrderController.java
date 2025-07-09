package cars.carbon.printService.controller;

import cars.carbon.printService.dto.WorkOrderRequestDTO;
import cars.carbon.printService.dto.workorder.EnfestoGroupDTO;
import cars.carbon.printService.dto.workorder.WorkOrderDTO;
import cars.carbon.printService.model.WorkOrders.WorkOrder;
import cars.carbon.printService.service.WorkOrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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


    @GetMapping("/plates-by-enfesto")
    public ResponseEntity<List<WorkOrderDTO>> getPlatesByEnfestoRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        List<WorkOrderDTO> result = workOrderService.findAllByEnfestoDateRange(startDateTime, endDateTime);
        return ResponseEntity.ok(result);
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
