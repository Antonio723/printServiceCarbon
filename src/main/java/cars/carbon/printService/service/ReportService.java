package cars.carbon.printService.service;

import cars.carbon.printService.dto.Etiqueta2DTO;
import cars.carbon.printService.dto.RenderRequestDTO;
import cars.carbon.printService.dto.WorkOrderRequestDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final DataSource dataSource;

    public ReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] gerarEtiqueta(Long otId) throws JRException {
        // Carrega o arquivo .jrxml do classpath
        InputStream jasperStream = this.getClass().getResourceAsStream("/Reports/BR_PROCUTION_LABEL.jrxml");
        if (jasperStream == null) {
            throw new JRException("Arquivo .jrxml não encontrado no classpath.");
        }
        // Compila o relatório
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);
        // Preenche os parâmetros
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("otid", otId);

        // Carrega a imagem do classpath como java.awt.Image
        InputStream imageStream = this.getClass().getResourceAsStream("/images/LOGO_OPERA.jpg");
        if (imageStream == null) {
            throw new JRException("Imagem do logo não encontrada no classpath.");
        }

        try {
            BufferedImage logoImage = ImageIO.read(imageStream);
            parametros.put("logo", logoImage);
        } catch (Exception e) {
            throw new JRException("Erro ao carregar imagem do logo", e);
        }

        // Abre conexão com o banco e gera o relatório
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

            // Exporta o relatório para PDF e retorna como array de bytes
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new JRException("Erro ao gerar relatório com conexão ao banco", e);
        }
    }

    public byte[] gerarEtiqueta2(Etiqueta2DTO dto) throws JRException {
        InputStream jasperStream = this.getClass().getResourceAsStream("/Reports/BR_LABEL_TEST_BODY.jrxml");
        if (jasperStream == null) {
            throw new JRException("Arquivo .jrxml não encontrado no classpath.");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

        // 🔥 Preenche os parâmetros com os dados do DTO
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("material", dto.getMaterial());
        parametros.put("process", dto.getProcess());
        parametros.put("plateBatch", dto.getPlateBatch());
        parametros.put("plastic", dto.getPlastic());
        parametros.put("plasticBatch", dto.getPlasticBatch());
        parametros.put("cloth", dto.getCloth());
        parametros.put("clothBatch", dto.getClothBatch());
        parametros.put("required", dto.getRequired());
        parametros.put("observation", dto.getObservation());

        // Carrega o logo
        InputStream imageStream = this.getClass().getResourceAsStream("/images/LOGO_OPERA_SEM_DESCRICAO.jpg");
        if (imageStream == null) {
            throw new JRException("Imagem do logo não encontrada no classpath.");
        }

        try {
            BufferedImage logoImage = ImageIO.read(imageStream);
            parametros.put("logo2", logoImage);
        } catch (Exception e) {
            throw new JRException("Erro ao carregar imagem do logo", e);
        }

        try (Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new JRException("Erro ao gerar relatório com conexão ao banco", e);
        }
    }

    public byte[] gerarEtiquetaReceipt(Long id) throws JRException {
        // Carrega o arquivo .jrxml do classpath
        InputStream jasperStream = this.getClass().getResourceAsStream("/Reports/BR_LABEL_RECEIPT.jrxml");
        if (jasperStream == null) {
            throw new JRException("Arquivo .jrxml não encontrado no classpath.");
        }
        // Compila o relatório
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);
        // Preenche os parâmetros
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("id", id);

        // Carrega a imagem do classpath como java.awt.Image
        InputStream imageStream = this.getClass().getResourceAsStream("/images/LOGO_OPERA.jpg");
        if (imageStream == null) {
            throw new JRException("Imagem do logo não encontrada no classpath.");
        }

        try {
            BufferedImage logoImage = ImageIO.read(imageStream);
            parametros.put("logo", logoImage);
        } catch (Exception e) {
            throw new JRException("Erro ao carregar imagem do logo", e);
        }

        // Abre conexão com o banco e gera o relatório
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

            // Exporta o relatório para PDF e retorna como array de bytes
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new JRException("Erro ao gerar relatório com conexão ao banco", e);
        }
    }

    /**
     * Render genérico, stateless: compila o .jrxml recebido no corpo, coage os
     * parâmetros para o tipo declarado em cada <parameter>, injeta imagens
     * (logo/logo2) do classpath e preenche. Se vier 'data', usa-o como
     * datasource (Map); caso contrário, usa o queryString SQL interno via DB.
     */
    public byte[] renderGeneric(RenderRequestDTO req) throws JRException {
        if (req == null || req.getJrxml() == null || req.getJrxml().isBlank()) {
            throw new JRException("jrxml é obrigatório no corpo da requisição.");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(
                new ByteArrayInputStream(req.getJrxml().getBytes(StandardCharsets.UTF_8)));

        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> incoming = req.getParams() != null ? req.getParams() : Map.of();

        // Coage cada parâmetro para o tipo declarado e injeta imagens server-side.
        for (JRParameter p : jasperReport.getParameters()) {
            if (p.isSystemDefined()) continue;
            String name = p.getName();
            String valueClass = p.getValueClassName();

            if (isImageClass(valueClass)) {
                parametros.put(name, loadLogo(name));
            } else if (incoming.containsKey(name)) {
                parametros.put(name, coerce(incoming.get(name), valueClass));
            }
        }

        try {
            JasperPrint jasperPrint;
            if (req.getData() != null && !req.getData().isEmpty()) {
                Collection<Map<String, ?>> rows = new ArrayList<>(req.getData());
                jasperPrint = JasperFillManager.fillReport(
                        jasperReport, parametros, new JRMapCollectionDataSource(rows));
            } else {
                try (Connection connection = dataSource.getConnection()) {
                    jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);
                }
            }
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            throw e;
        } catch (Exception e) {
            throw new JRException("Erro ao gerar relatório genérico", e);
        }
    }

    private boolean isImageClass(String className) {
        return "java.awt.image.BufferedImage".equals(className)
                || "java.awt.Image".equals(className);
    }

    private BufferedImage loadLogo(String paramName) throws JRException {
        // Convenção: 'logo2' usa a versão sem descrição; qualquer outro usa o logo padrão.
        String path = "logo2".equalsIgnoreCase(paramName)
                ? "/images/LOGO_OPERA_SEM_DESCRICAO.jpg"
                : "/images/LOGO_OPERA.jpg";
        try (InputStream imageStream = this.getClass().getResourceAsStream(path)) {
            if (imageStream == null) throw new JRException("Imagem não encontrada no classpath: " + path);
            return ImageIO.read(imageStream);
        } catch (JRException e) {
            throw e;
        } catch (Exception e) {
            throw new JRException("Erro ao carregar imagem do logo: " + path, e);
        }
    }

    // Converte o valor JSON (String/Number/Boolean) para o tipo Java declarado no .jrxml.
    private Object coerce(Object value, String valueClass) {
        if (value == null || valueClass == null) return value;
        String s = String.valueOf(value);
        try {
            switch (valueClass) {
                case "java.lang.Long":       return Long.valueOf(s.trim());
                case "java.lang.Integer":    return Integer.valueOf(s.trim());
                case "java.lang.Short":      return Short.valueOf(s.trim());
                case "java.lang.Double":     return Double.valueOf(s.trim());
                case "java.lang.Float":      return Float.valueOf(s.trim());
                case "java.math.BigDecimal": return new BigDecimal(s.trim());
                case "java.lang.Boolean":    return Boolean.valueOf(s.trim());
                default:                     return value; // String e demais tipos passam direto
            }
        } catch (NumberFormatException e) {
            return value; // deixa o Jasper reclamar se o tipo estiver realmente errado
        }
    }

    public byte[] gerarEtiquetareportEnfesto() throws JRException {
        // Carrega o arquivo .jrxml do classpath
        InputStream jasperStream = this.getClass().getResourceAsStream("/Reports/BR_REPORT_ENFESTO REPORT.jrxml");
        if (jasperStream == null) {
            throw new JRException("Arquivo .jrxml não encontrado no classpath.");
        }
        // Compila o relatório
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);
        // Preenche os parâmetros
        Map<String, Object> parametros = new HashMap<>();

        // Carrega a imagem do classpath como java.awt.Image
        InputStream imageStream = this.getClass().getResourceAsStream("/images/LOGO_OPERA.jpg");
        if (imageStream == null) {
            throw new JRException("Imagem do logo não encontrada no classpath.");
        }

        try {
            BufferedImage logoImage = ImageIO.read(imageStream);
            parametros.put("logo", logoImage);
        } catch (Exception e) {
            throw new JRException("Erro ao carregar imagem do logo", e);
        }

        // Abre conexão com o banco e gera o relatório
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

            // Exporta o relatório para PDF e retorna como array de bytes
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new JRException("Erro ao gerar relatório com conexão ao banco", e);
        }
    }
}
