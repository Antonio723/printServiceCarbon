package cars.carbon.printService.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Corpo do endpoint genérico POST /render.
 *
 * O Node (Orquestra_API) é a fonte da verdade dos relatórios Jasper: guarda o
 * .jrxml versionado e roda o JS que monta os parâmetros. Aqui o Spring apenas
 * compila o jrxml recebido e preenche, atuando como motor de render stateless.
 *
 *  - jrxml:  conteúdo cru do .jrxml (XML) da versão OPE.
 *  - params: mapa de parâmetros do relatório (ex.: { "id": 123 }).
 *  - data:   linhas opcionais; se presente, vira o datasource (JSON/Map) em vez
 *            de usar o queryString SQL interno do relatório.
 */
@Getter
@Setter
public class RenderRequestDTO {
    private String jrxml;
    private Map<String, Object> params;
    private List<Map<String, Object>> data;
}
