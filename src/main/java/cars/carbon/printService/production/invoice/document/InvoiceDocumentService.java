package cars.carbon.printService.production.invoice.document;

import cars.carbon.printService.production.cutting.exceptions.ResourceNotFoundException;
import cars.carbon.printService.production.invoice.model.Invoice;
import cars.carbon.printService.production.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceDocumentService {

    private final InvoiceDocumentRepository documentRepo;
    private final InvoiceRepository invoiceRepo;

    @Value("${invoice.documents.upload-dir:uploads/invoice-documents}")
    private String uploadDir;

    @Transactional
    public InvoiceDocumentResponse upload(String invoiceNumber,
                                          DocumentType type,
                                          MultipartFile file,
                                          String uploadedBy) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio");
        }

        Invoice invoice = invoiceRepo.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nota fiscal não encontrada: " + invoiceNumber));

        // Busca versão ativa anterior deste tipo
        InvoiceDocument previous = documentRepo
                .findByInvoice_InvoiceNumberAndTypeAndActiveTrue(invoiceNumber, type)
                .orElse(null);

        int nextVersion = (previous != null) ? previous.getVersion() + 1 : 1;

        // Persiste fisicamente
        Path storageDir = Paths.get(uploadDir, invoiceNumber, type.name().toLowerCase());
        Files.createDirectories(storageDir);

        String safeFilename = nextVersion + "_" + sanitize(file.getOriginalFilename());
        Path destination = storageDir.resolve(safeFilename);
        file.transferTo(destination);

        String hash = computeSha256(destination);

        InvoiceDocument newDoc = new InvoiceDocument();
        newDoc.setInvoice(invoice);
        newDoc.setType(type);
        newDoc.setOriginalFilename(file.getOriginalFilename());
        newDoc.setStoragePath(destination.toString());
        newDoc.setFileSizeBytes(file.getSize());
        newDoc.setSha256Hash(hash);
        newDoc.setVersion(nextVersion);
        newDoc.setActive(true);
        newDoc.setUploadedBy(uploadedBy);

        InvoiceDocument saved = documentRepo.save(newDoc);

        // Desativa a versão anterior e aponta para a nova
        if (previous != null) {
            previous.setActive(false);
            previous.setReplacedBy(saved);
            documentRepo.save(previous);
        }

        return toResponse(saved);
    }

    public List<InvoiceDocumentResponse> listActive(String invoiceNumber) {
        return documentRepo.findByInvoice_InvoiceNumberAndActiveTrue(invoiceNumber)
                .stream().map(this::toResponse).toList();
    }

    public List<InvoiceDocumentResponse> listHistory(String invoiceNumber) {
        return documentRepo.findByInvoice_InvoiceNumberOrderByVersionDesc(invoiceNumber)
                .stream().map(this::toResponse).toList();
    }

    // Retorna o arquivo para download
    public Path resolvePath(Long documentId) {
        InvoiceDocument doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento não encontrado: " + documentId));
        return Paths.get(doc.getStoragePath());
    }

    public String computeSha256(Path path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream is = Files.newInputStream(path)) {
                byte[] buf = new byte[8192];
                int read;
                while ((read = is.read(buf)) != -1) {
                    digest.update(buf, 0, read);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Erro ao calcular hash do arquivo", e);
        }
    }

    private String sanitize(String filename) {
        if (filename == null) return "documento.pdf";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private InvoiceDocumentResponse toResponse(InvoiceDocument d) {
        return new InvoiceDocumentResponse(
                d.getId(),
                d.getInvoice().getInvoiceNumber(),
                d.getType(),
                d.getOriginalFilename(),
                d.getFileSizeBytes(),
                d.getSha256Hash(),
                d.getVersion(),
                d.getActive(),
                d.getReplacedBy() != null ? d.getReplacedBy().getId() : null,
                d.getUploadedBy(),
                d.getCreatedAt()
        );
    }
}
