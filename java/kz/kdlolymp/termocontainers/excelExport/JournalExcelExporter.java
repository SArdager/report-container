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

public class JournalExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<ContainerNote> notes;
    private String startDate;
    private String endDate;
    private String departmentName;

    public JournalExcelExporter(List<ContainerNote> notes, String departmentName, String startDate, String endDate) {
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
        createCell(row, 1, "Отчет по движению термоконтейнеров", style);
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
        createCell(row, 2, "Номер термохрона", style);
        createCell(row, 3, "Дата отправки", style);
        createCell(row, 4, "Отправитель", style);
        createCell(row, 5, "Отправил", style);
        createCell(row, 6, "Запись отправителя", style);
        createCell(row, 7, "Сумма оплаты", style);
        createCell(row, 8, "Место оплаты", style);
        createCell(row, 9, "Получатель", style);
        createCell(row, 10, "Дата приемки", style);
        createCell(row, 11, "Получил", style);
        createCell(row, 12, "Запись получателя", style);
        createCell(row, 13, "Статус", style);
    }

    private void writeDataLines() {
        sheet.setDefaultColumnWidth(18);
        int rowNumber = 5;
        String status = "";
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (ContainerNote note : notes) {
            Row row = sheet.createRow(rowNumber);
            createCell(row, 0, note.getId(), style);
            createCell(row, 1, note.getContainer().getContainerNumber(), style);
            createCell(row, 2, note.getThermometer(), style);
            createCell(row, 3, note.getSendTime().format(formatter), style);
            createCell(row, 4, note.getOutDepartment().getBranch().getBranchName() + ", " + note.getOutDepartment().getDepartmentName(), style);
            createCell(row, 5, note.getOutUser().getUserSurname() + " " + note.getOutUser().getUserFirstname(), style);
            createCell(row, 6, note.getSendNote(), style);
            createCell(row, 7, note.getSendPay() + " тенге", style);
            if(note.isPaidEnd()){
                createCell(row, 8, "при получении", style);
            } else {
                createCell(row, 8, "при отправке", style);
            }
            createCell(row, 9, note.getToDepartment().getBranch().getBranchName() + ", " + note.getToDepartment().getDepartmentName(), style);
            if(note.getArriveTime()!=null){
                createCell(row, 10, note.getArriveTime().format(formatter), style);
                createCell(row, 11, note.getToUser().getUserSurname() + " " + note.getToUser().getUserFirstname(), style);
                createCell(row, 12, note.getArriveNote(), style);
                if(note.getDelayTime()>0){
                    status = "Опоздание: " + note.getDelayTime() + " часов";
                } else {
                    status = "Доставлено в срок";
                }
            } else {
                status = "В дороге";
            }
            createCell(row, 13, status, style);
            rowNumber++;
            if(note.getBetweenPoints()!=null){
                for(BetweenPoint point: note.getBetweenPoints()){
                    row = sheet.createRow(rowNumber);
                    createCell(row, 6, "промежуточный объект: ", style);
                    createCell(row, 9, point.getDepartment().getDepartmentName() + ", " + point.getDepartment().getBranch().getBranchName(), style);
                    createCell(row, 10, point.getPassTime().format(formatter), style);
                    createCell(row, 11, point.getPassUser().getUserSurname() + " " + point.getPassUser().getUserFirstname(), style);
                    createCell(row, 12, point.getPointNote(), style);
                    rowNumber++;
                }
            }
        }
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
