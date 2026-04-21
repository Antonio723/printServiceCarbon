package cars.carbon.printService.production.invoice.document;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/invoices/{invoiceNumber}/documents")
@RequiredArgsConstructor
public class InvoiceDocumentController {

    private final InvoiceDocumentService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InvoiceDocumentResponse> upload(
            @PathVariable String invoiceNumber,
            @RequestParam DocumentType type,
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "sistema") String uploadedBy) throws IOException {

        InvoiceDocumentResponse response = service.upload(invoiceNumber, type, file, uploadedBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<InvoiceDocumentResponse> listActive(@PathVariable String invoiceNumber) {
        return service.listActive(invoiceNumber);
    }

    @GetMapping("/history")
    public List<InvoiceDocumentResponse> listHistory(@PathVariable String invoiceNumber) {
        return service.listHistory(invoiceNumber);
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> download(@PathVariable String invoiceNumber,
                                             @PathVariable Long documentId) {
        Path path = service.resolvePath(documentId);
        Resource resource = new PathResource(path);
        String encoded = URLEncoder.encode(path.getFileName().toString(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
