package kz.kdlolymp.termocontainers.excelExport;

import kz.kdlolymp.termocontainers.entity.BetweenPoint;
import kz.kdlolymp.termocontainers.entity.Container;
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

public class RouteExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<ContainerNote> notes;
    private String startDate;
    private String endDate;
    private Container container;

    public RouteExcelExporter(List<ContainerNote> notes, Container container, String startDate, String endDate) {
        this.notes = notes;
        this.container = container;
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
        createCell(row, 1, "Отчет по использованию термоконтейнера", style);
    }
    private void writeTitleLine(){
        sheet.setDefaultColumnWidth(24);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Row row = sheet.createRow(1);
        createCell(row, 1, "№ термоконтейнера:", style);
        createCell(row, 2, container.getContainerNumber(), style);
        row = sheet.createRow(2);
        createCell(row, 1, "Характеристика:", style);
        createCell(row, 2, container.getValue().getValueName(), style);
        row = sheet.createRow(3);
        createCell(row, 1, "Начало эксплуатации:", style);
        createCell(row, 2, container.getRegistrationDate().format(formatter), style);
        row = sheet.createRow(4);
        createCell(row, 1, "Место нахождения:", style);
        createCell(row, 2, container.getDepartment().getDepartmentName() + ", " + container.getDepartment().getBranch().getBranchName(), style);
        row = sheet.createRow(5);
        createCell(row, 1, "С даты:", style);
        createCell(row, 2, startDate, style);
        row = sheet.createRow(6);
        createCell(row, 1, "На дату:", style);
        createCell(row, 2, endDate, style);
        row = sheet.createRow(7);
        createCell(row, 0, "Номер", style);
        createCell(row, 1, "Дата отправки", style);
        createCell(row, 2, "Отправитель", style);
        createCell(row, 3, "Получатель", style);
        createCell(row, 4, "Дата прибытия", style);
        createCell(row, 5, "Статус", style);
        createCell(row, 6, "Запись отправителя", style);
        createCell(row, 7, "Запись получателя", style);
    }

    private void writeDataLines() {
        sheet.setDefaultColumnWidth(24);
        int rowNumber = 8;
        String status = "";
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (ContainerNote note : notes) {
            Row row = sheet.createRow(rowNumber);
            createCell(row, 0, note.getId(), style);
            createCell(row, 1, note.getSendTime().format(formatter), style);
            createCell(row, 2, note.getOutDepartment().getBranch().getBranchName() + ", " + note.getOutDepartment().getDepartmentName(), style);
            createCell(row, 3, note.getToDepartment().getBranch().getBranchName() + ", " + note.getToDepartment().getDepartmentName(), style);
            if(note.getArriveTime()!=null){
                createCell(row, 4, note.getArriveTime().format(formatter), style);
                if(note.getDelayTime()>0){
                    status = "Опоздание: " + note.getDelayTime() + " часов";
                } else {
                    status = "Доставлено в срок";
                }
            } else {
                status = "В дороге";
            }
            createCell(row, 5, status, style);
            createCell(row, 6, note.getSendNote(), style);
            createCell(row, 7, note.getArriveNote(), style);
            rowNumber++;
            if(note.getBetweenPoints()!=null){
                for(BetweenPoint point: note.getBetweenPoints()){
                    row = sheet.createRow(rowNumber);
                    createCell(row, 2, "через объект: ", style);
                    createCell(row, 3, point.getDepartment().getDepartmentName() + ", " + point.getDepartment().getBranch().getBranchName(), style);
                    createCell(row, 4, point.getPassTime().format(formatter), style);
                    createCell(row, 6, point.getPointNote(), style);
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
