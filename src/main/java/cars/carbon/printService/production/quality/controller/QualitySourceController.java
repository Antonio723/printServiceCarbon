package cars.carbon.printService.production.quality.controller;

import cars.carbon.printService.production.quality.dto.QualitySourceResponse;
import cars.carbon.printService.production.quality.service.QualitySourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class QualitySourceController {

    private final QualitySourceService service;

    /**
     * Retorna os dados de origem (NF + corte + enfestos) que o Maestro usa para
     * pré-preencher um novo Certificado de Qualidade.
     */
    @GetMapping("/{number}/quality-source")
    public QualitySourceResponse getQualitySource(@PathVariable("number") String number) {
        return service.findByInvoiceNumber(number);
    }
}
