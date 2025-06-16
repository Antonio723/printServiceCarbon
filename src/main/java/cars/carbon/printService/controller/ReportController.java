package cars.carbon.printService.controller;
import cars.carbon.printService.dto.Etiqueta2DTO;
import cars.carbon.printService.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReportController {
    private final ReportService etiquetaService;

    public ReportController(ReportService etiquetaService) {
        this.etiquetaService = etiquetaService;
    }

    @GetMapping("/etiqueta")
    public void gerarEtiqueta(@RequestParam Long otid, HttpServletResponse response) throws Exception {
        byte[] pdf = etiquetaService.gerarEtiqueta(otid);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=etiqueta.pdf");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("X-Frame-Options", "ALLOWALL");

        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    @GetMapping("/etiqueta2")
    public void gerarEtiqueta2(
            @RequestParam String material,
            @RequestParam String process,
            @RequestParam String plateBatch,
            @RequestParam String plastic,
            @RequestParam String plasticBatch,
            @RequestParam String cloth,
            @RequestParam String clothBatch,
            @RequestParam String required,
            @RequestParam(required = false) String observation,
            HttpServletResponse response) throws Exception {

        Etiqueta2DTO dto = new Etiqueta2DTO();
        dto.setMaterial(material);
        dto.setProcess(process);
        dto.setPlateBatch(plateBatch);
        dto.setPlastic(plastic);
        dto.setPlasticBatch(plasticBatch);
        dto.setCloth(cloth);
        dto.setClothBatch(clothBatch);
        dto.setRequired(required);
        dto.setObservation(observation);

        byte[] pdf = etiquetaService.gerarEtiqueta2(dto);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=etiqueta2.pdf");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("X-Frame-Options", "ALLOWALL");

        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    @GetMapping("/etiquetaReceipt")
    public void gerarEtiquetaReceipt(@RequestParam Long id, HttpServletResponse response) throws Exception {
        byte[] pdf = etiquetaService.gerarEtiquetaReceipt(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=etiqueta.pdf");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("X-Frame-Options", "ALLOWALL");

        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }
}
