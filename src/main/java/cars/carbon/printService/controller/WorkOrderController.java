package cars.carbon.printService.controller;

import cars.carbon.printService.dto.WorkOrderRequestDTO;
import cars.carbon.printService.dto.workorder.EnfestoGroupDTO;
import cars.carbon.printService.dto.workorder.WorkOrderDTO;
import cars.carbon.printService.model.WorkOrders.WorkOrder;
import cars.carbon.printService.service.WorkOrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.io.IOException;

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
    public ResponseEntity<List<EnfestoGroupDTO>> getPlatesByEnfestoRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        List<EnfestoGroupDTO> result = workOrderService.findAllByEnfestoDateRange(start, end);
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

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) throws IOException {

        byte[] excelBytes = workOrderService.generateExcelReport(start, end);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_enfesto.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
