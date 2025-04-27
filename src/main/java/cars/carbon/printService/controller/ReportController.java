package cars.carbon.printService.controller;
import cars.carbon.printService.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
