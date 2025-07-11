package cars.carbon.printService.service;

import cars.carbon.printService.dto.WorkOrderRequestDTO;
import cars.carbon.printService.model.WorkOrders.Plates;
import cars.carbon.printService.model.WorkOrders.WorkOrder;
import cars.carbon.printService.repository.WorkOrderRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cars.carbon.printService.dto.workorder.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Transactional
    public WorkOrder createWorkOrder(WorkOrderRequestDTO dto) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setCreationDate(LocalDateTime.now());
        workOrder.setChangeDate(LocalDateTime.now());
        workOrder.setLote(dto.getLote());
        workOrder.setClothType(dto.getClothType());
        workOrder.setPlasticType(dto.getPlasticType());
        workOrder.setPlasticBatch(dto.getPlasticBatch());
        workOrder.setClothBatch(dto.getClothBatch());
        workOrder.setPlatesQuantity(dto.getPlatesQuantity());
        workOrder.setPlatesLayres(dto.getPlatesLayres());
        workOrder.setResinedBatch(dto.getResinedBatch());
        workOrder.setEnfestoDate(dto.getEnfestoDate());

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        List<Plates> platesList = new ArrayList<>();
        for (long i = 1; i <= dto.getPlatesQuantity(); i++) {
            Plates plate = new Plates();
            plate.setPlateSequence(i);
            plate.setWorkorderid(savedWorkOrder);
            platesList.add(plate);
        }

        savedWorkOrder.setPlates(platesList);
        return workOrderRepository.save(savedWorkOrder);
    }

    @Transactional
    public WorkOrder updateWorkOrder(Long id, WorkOrderRequestDTO dto) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de trabalho não encontrada para o ID: " + id));

        workOrder.setChangeDate(LocalDateTime.now());
        workOrder.setLote(dto.getLote());
        workOrder.setClothType(dto.getClothType());
        workOrder.setPlasticType(dto.getPlasticType());
        workOrder.setPlasticBatch(dto.getPlasticBatch());
        workOrder.setClothBatch(dto.getClothBatch());
        workOrder.setPlatesQuantity(dto.getPlatesQuantity());
        workOrder.setPlatesLayres(dto.getPlatesLayres());
        workOrder.setResinedBatch(dto.getResinedBatch());
        workOrder.setEnfestoDate(dto.getEnfestoDate());

        return workOrderRepository.save(workOrder);
    }

    @Transactional
    public List<WorkOrder> listAll() {
        return workOrderRepository.findAll();
    }

    @Transactional
    public String deleteAllById(Long id) {
        if (workOrderRepository.existsById(id)) {
            workOrderRepository.deleteById(id);
            return "Ordem de trabalho deletada com sucesso.";
        } else {
            return "Ordem de trabalho com ID " + id + " não encontrada.";
        }
    }

    @Transactional
    public List<EnfestoGroupDTO> listGroupedByEnfestoDate() {
        List<WorkOrder> allOrders = workOrderRepository.findAll();

        Map<LocalDate, List<WorkOrder>> groupedByDate = allOrders.stream()
                .collect(Collectors.groupingBy(w -> w.getEnfestoDate()));

        List<EnfestoGroupDTO> result = new ArrayList<>();

        for (Map.Entry<LocalDate, List<WorkOrder>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<WorkOrder> workOrders = entry.getValue();

            long totalPlacas = workOrders.stream()
                    .mapToLong(WorkOrder::getPlatesQuantity)
                    .sum();

            List<WorkOrderDTO> workOrderDTOs = workOrders.stream().map(w -> {
                WorkOrderDTO dto = new WorkOrderDTO();
                dto.setId(w.getId());
                dto.setLote(w.getLote());
                dto.setPlatesQuantity(w.getPlatesQuantity());
                dto.setPlatesLayres(w.getPlatesLayres());
                dto.setClothBatch(w.getClothBatch());
                dto.setPlasticType(w.getPlasticType());
                dto.setPlasticBatch(w.getPlasticBatch());
                dto.setResinedBatch(w.getResinedBatch());
                dto.setPlates(w.getPlates());
                return dto;
            }).collect(Collectors.toList());

            EnfestoGroupDTO groupDTO = new EnfestoGroupDTO();
            groupDTO.setEnfestoDate(date);
            groupDTO.setTotalPlacas(totalPlacas);
            groupDTO.setWorkOrders(workOrderDTOs);

            result.add(groupDTO);
        }

        return result;
    }

    @Transactional
    public List<WorkOrderDTO> findAllByEnfestoDateRange(LocalDate start, LocalDate end) {
        List<WorkOrder> workOrders = workOrderRepository.findByEnfestoDateBetween(start, end);

        return workOrders.stream().map(w -> {
            WorkOrderDTO dto = new WorkOrderDTO();
            dto.setId(w.getId());
            dto.setLote(w.getLote());
            dto.setPlatesQuantity(w.getPlatesQuantity());
            dto.setPlatesLayres(w.getPlatesLayres());
            dto.setClothType(w.getClothType());
            dto.setClothBatch(w.getClothBatch());
            dto.setPlasticType(w.getPlasticType());
            dto.setPlasticBatch(w.getPlasticBatch());
            dto.setResinedBatch(w.getResinedBatch());
            dto.setPlates(w.getPlates());
            return dto;
        }).collect(Collectors.toList());
    }

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

        String[] headers = {"ID", "Lote", "Qtd Placas", "Camadas", "Tecido", "Lote Tecido", "Plástico", "Lote Plástico", "RWO", "Data Enfesto"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (WorkOrder wo : workOrders) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(wo.getId());
            row.createCell(1).setCellValue(wo.getLote());
            row.createCell(2).setCellValue(wo.getPlatesQuantity());
            row.createCell(3).setCellValue(wo.getPlatesLayres());
            row.createCell(4).setCellValue(wo.getClothType());
            row.createCell(5).setCellValue(wo.getClothBatch());
            row.createCell(6).setCellValue(wo.getPlasticType());
            row.createCell(7).setCellValue(wo.getPlasticBatch());
            row.createCell(8).setCellValue(wo.getResinedBatch());
            row.createCell(9).setCellValue(wo.getEnfestoDate().toString());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createSummarySheet(Workbook workbook, List<WorkOrder> workOrders) {
        Sheet sheet = workbook.createSheet("Resumo");

        // Criação de estilos
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

        // Estilo para todas as células com bordas
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
            int firstDataRow = rowNum + 2; // Lembra a primeira linha de dados
            int lastDataRow = firstDataRow; // Será atualizado conforme adicionamos linhas

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

            long monthTotal = 0;

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

                    monthTotal += layerEntry.getValue();
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
            // Cria a fórmula SUM para somar a coluna C (índice 2) das linhas de dados
            totalValueCell.setCellFormula("SUM(C" + (firstDataRow+1) + ":C" + (lastDataRow+1) + ")");
            totalValueCell.setCellStyle(mergedCellStyle);

            rowNum++;
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // Método auxiliar para aplicar estilo a todas as células de uma região mesclada
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
