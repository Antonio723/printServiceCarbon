package cars.carbon.printService.service;

import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
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
}
