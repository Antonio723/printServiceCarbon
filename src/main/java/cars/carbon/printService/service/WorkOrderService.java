package cars.carbon.printService.service;

import cars.carbon.printService.model.WorkOrders.WorkOrder;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.repository.WorkOrderRepository;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    public byte[] generateExcelReport(LocalDate start, LocalDate end) throws IOException {
        List<WorkOrder> workOrders = workOrderRepository.findByEnfestoDateBetween(start, end);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            createDetailsSheet(workbook, workOrders);
            createSummarySheet(workbook, workOrders);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createDetailsSheet(Workbook workbook, List<WorkOrder> workOrders) {
        Sheet sheet = workbook.createSheet("Detalhes");

        CreationHelper creationHelper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        String[] headers = {"NUMERO DA PLACA", "OT", "LOTE", "Qtd PLACAS", "CAMADAS", "TECIDO", "LOTE TECIDO",
                "PLASTICO", "LOTE PLASTICO", "RWO", "DATA ENFESTO", "SEQUENCIA DA PLACA", "STATUS DA PLACA"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowIdx = 1;
        for (WorkOrder wo : workOrders) {
            if (wo.getPlates() != null) {
                for (Plates plate : wo.getPlates()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(plate.getId() + "-" + plate.getWorkorderid().getId());
                    row.createCell(1).setCellValue(wo.getId());
                    row.createCell(2).setCellValue(wo.getLote());
                    row.createCell(3).setCellValue(wo.getPlatesQuantity());
                    row.createCell(4).setCellValue(wo.getPlatesLayres());
                    row.createCell(5).setCellValue(wo.getClothType());
                    row.createCell(6).setCellValue(wo.getClothBatch());
                    row.createCell(7).setCellValue(wo.getPlasticType());
                    row.createCell(8).setCellValue(wo.getPlasticBatch());
                    row.createCell(9).setCellValue(wo.getResinedBatch());

                    Cell dateCell = row.createCell(10);
                    dateCell.setCellValue(java.sql.Date.valueOf(wo.getEnfestoDate().atStartOfDay().toLocalDate()));
                    dateCell.setCellStyle(dateCellStyle);

                    row.createCell(11).setCellValue(plate.getPlateSequence());
                    row.createCell(12).setCellValue(plate.getStatus().name());
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createSummarySheet(Workbook workbook, List<WorkOrder> workOrders) {
        Sheet sheet = workbook.createSheet("Resumo");

        Font boldFont = workbook.createFont();
        boldFont.setBold(true);

        CellStyle mergedCellStyle = workbook.createCellStyle();
        mergedCellStyle.setFont(boldFont);
        mergedCellStyle.setAlignment(HorizontalAlignment.CENTER);
        mergedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        mergedCellStyle.setBorderBottom(BorderStyle.THIN);
        mergedCellStyle.setBorderTop(BorderStyle.THIN);
        mergedCellStyle.setBorderLeft(BorderStyle.THIN);
        mergedCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(boldFont);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setBorderBottom(BorderStyle.THIN);
        titleCellStyle.setBorderTop(BorderStyle.THIN);
        titleCellStyle.setBorderLeft(BorderStyle.THIN);
        titleCellStyle.setBorderRight(BorderStyle.THIN);
        titleCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.cloneStyleFrom(mergedCellStyle);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle allStyle = workbook.createCellStyle();
        allStyle.setBorderBottom(BorderStyle.THIN);
        allStyle.setBorderTop(BorderStyle.THIN);
        allStyle.setBorderLeft(BorderStyle.THIN);
        allStyle.setBorderRight(BorderStyle.THIN);
        allStyle.setAlignment(HorizontalAlignment.CENTER);
        allStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Map<YearMonth, Map<String, Map<Long, Long>>> summary = workOrders.stream()
                .collect(Collectors.groupingBy(
                        wo -> YearMonth.from(wo.getEnfestoDate()),
                        Collectors.groupingBy(
                                WorkOrder::getLote,
                                Collectors.groupingBy(
                                        WorkOrder::getPlatesLayres,
                                        Collectors.summingLong(WorkOrder::getPlatesQuantity)
                                )
                        )
                ));

        int rowNum = 0;
        for (Map.Entry<YearMonth, Map<String, Map<Long, Long>>> monthEntry : summary.entrySet()) {
            int firstDataRow = rowNum + 2;
            int lastDataRow = firstDataRow;

            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Resumo " +
                    monthEntry.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR")) +
                    " " + monthEntry.getKey().getYear());

            CellRangeAddress titleRegion = new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2);
            sheet.addMergedRegion(titleRegion);
            applyStyleToMergedRegion(sheet, titleRegion, titleCellStyle);

            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Camadas", "Lote", "Total Placas"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (Map.Entry<String, Map<Long, Long>> batchEntry : monthEntry.getValue().entrySet()) {
                for (Map.Entry<Long, Long> layerEntry : batchEntry.getValue().entrySet()) {
                    Row row = sheet.createRow(rowNum++);

                    Cell cell1 = row.createCell(0);
                    cell1.setCellValue(layerEntry.getKey() + " Camadas");
                    cell1.setCellStyle(allStyle);

                    Cell cell2 = row.createCell(1);
                    cell2.setCellValue(batchEntry.getKey());
                    cell2.setCellStyle(allStyle);

                    Cell cell3 = row.createCell(2);
                    cell3.setCellValue(layerEntry.getValue());
                    cell3.setCellStyle(allStyle);

                    lastDataRow = rowNum - 1;
                }
            }

            Row totalRow = sheet.createRow(rowNum++);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL");

            CellRangeAddress totalLabelRegion = new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 1);
            sheet.addMergedRegion(totalLabelRegion);
            applyStyleToMergedRegion(sheet, totalLabelRegion, mergedCellStyle);

            Cell totalValueCell = totalRow.createCell(2);
            totalValueCell.setCellFormula("SUM(C" + (firstDataRow + 1) + ":C" + (lastDataRow + 1) + ")");
            totalValueCell.setCellStyle(mergedCellStyle);

            rowNum++;
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void applyStyleToMergedRegion(Sheet sheet, CellRangeAddress region, CellStyle style) {
        for (int row = region.getFirstRow(); row <= region.getLastRow(); row++) {
            Row currentRow = sheet.getRow(row);
            if (currentRow == null) currentRow = sheet.createRow(row);
            for (int col = region.getFirstColumn(); col <= region.getLastColumn(); col++) {
                Cell cell = currentRow.getCell(col);
                if (cell == null) cell = currentRow.createCell(col);
                cell.setCellStyle(style);
            }
        }
    }
}
