package kz.kdlolymp.termocontainers.excelExport;

import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.service.DepartmentService;
import kz.kdlolymp.termocontainers.service.UserService;
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

public class LogsExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    List<EventLog> logs;
    private int eventId;
    private String branchName;
    private String startDate;
    private String endDate;
    private UserService userService;
    private DepartmentService departmentService;

    public LogsExcelExporter(List<EventLog> logs, int eventId, String branchName, String startDate,
               String endDate, UserService userService, DepartmentService departmentService) {
        this.logs = logs;
        this.eventId = eventId;
        this.branchName = branchName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userService = userService;
        this.departmentService = departmentService;
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine(){
        sheet = workbook.createSheet("Logs");
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        Row row = sheet.createRow(0);
        if(eventId == 1) {
            createCell(row, 1, "Отчет по событиям смены времени доставки", style);
        } else {
            createCell(row, 1, "Отчет по событиям смены прав доступа", style);
        }
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
        createCell(row, 2, branchName, style);
        row = sheet.createRow(2);
        createCell(row, 1, "С даты:", style);
        createCell(row, 2, startDate, style);
        row = sheet.createRow(3);
        createCell(row, 1, "На дату:", style);
        createCell(row, 2, endDate, style);
        row = sheet.createRow(4);
        createCell(row, 0, "Id", style);
        createCell(row, 1, "Дата смены", style);
        createCell(row, 2, "Филиал", style);
        if(eventId == 1) {
            createCell(row, 3, "Пункт отправки", style);
            createCell(row, 4, "Пункт доставки", style);
            createCell(row, 5, "Старое время в пути", style);
            createCell(row, 6, "Новое время в пути", style);
        } else {
            createCell(row, 3, "Подразделение", style);
            createCell(row, 4, "Пользователь", style);
            createCell(row, 5, "Старые права по объекту", style);
            createCell(row, 6, "Новые права по объекту", style);
        }
        createCell(row, 7, "Внес изменения", style);
    }

    private void writeDataLines() {
        sheet.setDefaultColumnWidth(18);
        int rowNumber = 5;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (EventLog log : logs) {
            Row row = sheet.createRow(rowNumber);
            createCell(row, 0, log.getId(), style);
            createCell(row, 1, log.getTime().format(formatter), style);
            createCell(row, 2, log.getBranch().getBranchName(), style);
            createCell(row, 3, log.getDepartment().getDepartmentName() + ", " + log.getDepartment().getBranch().getBranchName(), style);
            String target;
            if(eventId == 1) {
                Department department = departmentService.findDepartmentById(Integer.parseInt(log.getTarget() + ""));
                target = department.getDepartmentName() + ", " + department.getBranch().getBranchName();
            } else {
                User user = userService.findUserById(log.getTarget());
                target = user.getPosition() + " " + user.getUserSurname() + " " + user.getUserFirstname();
            }
            createCell(row, 4, target, style);
            createCell(row, 5, log.getOldValue(), style);
            createCell(row, 6, log.getNewValue(), style);
            createCell(row, 7, log.getUser().getPosition() + " " + log.getUser().getUserSurname() + " " + log.getUser().getUserFirstname(), style);
            rowNumber++;
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
