package kz.kdlolymp.termocontainers.excelExport;

import kz.kdlolymp.termocontainers.entity.BetweenPoint;
import kz.kdlolymp.termocontainers.entity.ContainerNote;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PayExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<ContainerNote> notes;
    private String startDate;
    private String endDate;
    private String departmentName;

    public PayExcelExporter(List<ContainerNote> notes, String departmentName, String startDate, String endDate) {
        this.notes = notes;
        this.departmentName = departmentName;
        this.startDate = startDate;
        this.endDate = endDate;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine(){
        sheet = workbook.createSheet("Notes");
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        Row row = sheet.createRow(0);
        createCell(row, 1, "Отчет по оплатам за транспортировку термоконтейнеров", style);
    }
    private void writeTitleLine(){
        sheet.setDefaultColumnWidth(18);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);
        Row row = sheet.createRow(1);
        createCell(row, 1, "по филиалу:", style);
        createCell(row, 2, departmentName, style);
        row = sheet.createRow(2);
        createCell(row, 1, "С даты:", style);
        createCell(row, 2, startDate, style);
        row = sheet.createRow(3);
        createCell(row, 1, "На дату:", style);
        createCell(row, 2, endDate, style);
        row = sheet.createRow(4);
        createCell(row, 0, "Номер", style);
        createCell(row, 1, "Номер контейнера", style);
        createCell(row, 2, "Дата отправки", style);
        createCell(row, 3, "Отправитель", style);
        createCell(row, 4, "Получатель", style);
        createCell(row, 5, "Сумма оплаты", style);
        createCell(row, 6, "Запись отправителя", style);
    }

    private void writeDataLines() {
        sheet.setDefaultColumnWidth(18);
        int rowNumber = 5;
        Long total = 0L;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        CellStyle boldStyle = workbook.createCellStyle();
        XSSFFont boldFont = workbook.createFont();
        boldFont.setFontHeight(12);
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (ContainerNote note : notes) {
            Row row = sheet.createRow(rowNumber);
            createCell(row, 0, note.getId(), style);
            createCell(row, 1, note.getContainer().getContainerNumber(), style);
            createCell(row, 2, note.getSendTime().format(formatter), style);
            createCell(row, 3, note.getOutDepartment().getBranch().getBranchName() + ", " + note.getOutDepartment().getDepartmentName(), style);
            createCell(row, 4, note.getToDepartment().getBranch().getBranchName() + ", " + note.getToDepartment().getDepartmentName(), style);
            createCell(row, 5, note.getSendPay(), style);
            createCell(row, 6, note.getSendNote(), style);
            total += note.getSendPay();
            rowNumber++;
        }
        Row row = sheet.createRow(rowNumber);
        createCell(row, 4, "Всего:", boldStyle);
        createCell(row, 5, total, boldStyle);
    }
    private void createCell(Row row, int columnNumber, Object value, CellStyle style) {
        Cell cell = row.createCell(columnNumber);
        if(value instanceof Integer){
            cell.setCellValue((Integer) value);
        } else if(value instanceof Long){
            cell.setCellValue((Long) value);
        } else if(value instanceof Boolean){
            cell.setCellValue((Boolean)value);
        } else{
            cell.setCellValue((String)value);
        }
        cell.setCellStyle(style);
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeTitleLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


}
