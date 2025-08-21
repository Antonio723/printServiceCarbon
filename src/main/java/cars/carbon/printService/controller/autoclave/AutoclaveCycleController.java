package cars.carbon.printService.controller.autoclave;

import cars.carbon.printService.dto.autoclave.AutoclaveCycleDTO;
import cars.carbon.printService.dto.autoclave.AutoclaveCycleWithDetailsDTO;
import cars.carbon.printService.enums.CycleStatus;
import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import cars.carbon.printService.service.AutoclaveCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/autoclave/cycle")
public class AutoclaveCycleController {

    @Autowired
    private AutoclaveCycleService cycleService;

    @PostMapping
    public ResponseEntity<AutoclaveCycle> createCycle(@RequestBody AutoclaveCycleDTO dto) {
        return ResponseEntity.ok(cycleService.createCycle(dto));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<AutoclaveCycle> uploadReport(@PathVariable Long id,
                                                       @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(cycleService.uploadReport(id, file));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AutoclaveCycle> updateStatus(@PathVariable Long id,
                                                       @RequestParam CycleStatus status) {
        return ResponseEntity.ok(cycleService.updateStatus(id, status));
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<AutoclaveCycle> duplicateCycle(@PathVariable Long id) {
        return ResponseEntity.ok(cycleService.duplicateCycle(id));
    }

    @GetMapping
    public ResponseEntity<List<AutoclaveCycle>> getAllCycles() {
        return ResponseEntity.ok(cycleService.getAll());
    }

    @GetMapping("/summary")
    public ResponseEntity<List<AutoclaveCycleWithDetailsDTO>> getCycleSummaries() {
        return ResponseEntity.ok(cycleService.listDetailedCycles());
    }
}