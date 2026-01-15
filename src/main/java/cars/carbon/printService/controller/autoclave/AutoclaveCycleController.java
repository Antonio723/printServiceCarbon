package cars.carbon.printService.controller.autoclave;

import cars.carbon.printService.dto.autoclave.AutoclaveCycleDTO;
import cars.carbon.printService.dto.autoclave.AutoclaveCycleWithDetailsDTO;
import cars.carbon.printService.dto.autoclave.ChangeStatus;
import cars.carbon.printService.dto.autoclave.CompletCycle;
import cars.carbon.printService.dto.workorder.EnfestoGroupDTO;
import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import cars.carbon.printService.service.AutoclaveCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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

    @PostMapping("/complete/{id}/upload")
    public ResponseEntity<AutoclaveCycle> completeCycleWithImage(@PathVariable Long id,
                                                       @RequestPart("file") MultipartFile file,
                                                        @RequestPart("data") CompletCycle data) throws IOException {
        return ResponseEntity.ok(cycleService.completeCycleWithImage(id, file, data));
    }

    /*@PostMapping("/complete/{id}")
    public ResponseEntity<AutoclaveCycle> completeCycle(@PathVariable Long id,
                                                        @RequestPart("file") MultipartFile file,
                                                        @RequestPart("data") CompletCycle data) throws IOException {
        return ResponseEntity.ok(cycleService.completeCycleWithImage(id, file, data));
    }*/

    @PatchMapping("/{id}/status")
    public ResponseEntity<AutoclaveCycle> updateStatus(@PathVariable Long id,
                                                       @RequestBody ChangeStatus dto) {
        return ResponseEntity.ok(cycleService.updateStatus(id, dto));
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

    @GetMapping("/by-cycle")
    public ResponseEntity<List<AutoclaveCycleWithDetailsDTO>> getCyclesByRangeDate(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        List<AutoclaveCycleWithDetailsDTO> result = cycleService.findByByDateRange(start, end);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/report/{filename}")
    public ResponseEntity<Resource> getReportFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(System.getProperty("user.dir"), "uploads", "autoclave-reports", filename);
        UrlResource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        // Detecta o tipo automaticamente (imagem, pdf, etc)
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

}