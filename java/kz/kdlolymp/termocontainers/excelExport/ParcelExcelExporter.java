package kz.kdlolymp.termocontainers.excelExport;

import kz.kdlolymp.termocontainers.entity.ParcelPoint;
import kz.kdlolymp.termocontainers.entity.Parcel;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParcelExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Parcel> parcels;
    private String startDate;
    private String endDate;
    private String departmentName;

    public ParcelExcelExporter(List<Parcel> parcels, String departmentName, String startDate, String endDate) {
        this.parcels = parcels;
        this.departmentName = departmentName;
        this.startDate = startDate;
        this.endDate = endDate;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine(){
        sheet = workbook.createSheet("Parcels");
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        Row row = sheet.createRow(0);
        createCell(row, 1, "Отчет по движению почтовых отправлений", style);
    }
    private void writeTitleLine(){
        sheet.setDefaultColumnWidth(14);
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
        createCell(row, 0, "Номер отправления", style);
        createCell(row, 1, "Вид отправления", style);
        createCell(row, 2, "Габариты", style);
        createCell(row, 3, "Отправитель", style);
        createCell(row, 4, "Подготовил", style);
        createCell(row, 5, "Отправил", style);
        createCell(row, 6, "Дата отправки", style);
        createCell(row, 7, "Получатель", style);
        createCell(row, 8, "Статус", style);
        createCell(row, 9, "Дата доставки", style);
        createCell(row, 10, "Пункт отгрузки", style);
        createCell(row, 11, "Дата получения", style);
        createCell(row, 12, "Получил", style);
        createCell(row, 13, "Дата отгрузки", style);
        createCell(row, 14, "Отправил", style);
        createCell(row, 15, "Вложено в", style);
        createCell(row, 16, "Стоимость доставки", style);
        createCell(row, 17, "Примечание", style);
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
        for (Parcel parcel : parcels) {
            Row row = sheet.createRow(rowNumber);
            createCell(row, 0, parcel.getParcelNumber(), style);
            String parcelLetter = parcel.getParcelNumber().substring(0, 1);
            String parcelType = "";
            switch (parcelLetter) {
                case ("K"):
                    parcelType = "Документы, корреспонденция";
                    break;
                case ("P"):
                    parcelType = "Реагенты";
                    break;
                case ("M"):
                    parcelType = "Материалы";
                    break;
                case ("O"):
                    parcelType = "Запасные части";
                    break;
                case ("C"):
                    parcelType = "Компьютерное оборудование";
                    break;
                default:
                    parcelType = "";
                    break;
            }
            createCell(row, 1, parcelType, style);
            createCell(row, 2, parcel.getDimensions(), style);
            createCell(row, 3, parcel.getOutDepartment().getBranch().getBranchName() + ", " + parcel.getOutDepartment().getDepartmentName(), style);
            createCell(row, 4, parcel.getCreateUser().getUserSurname() + " " + parcel.getCreateUser().getUserFirstname(), style);
            if(parcel.getSendUser()!=null){
                createCell(row, 5, parcel.getSendUser().getUserSurname() + " " + parcel.getSendUser().getUserFirstname(), style);
            }
            if(parcel.getSendDate()!=null){
                createCell(row, 6, parcel.getSendDate().format(formatter), style);
            }
            if(parcel.getDestination()!=null) {
                createCell(row, 7, parcel.getDestination().getBranch().getBranchName() + ", " + parcel.getDestination().getDepartmentName(), style);
            }
            if(parcel.getDeliveryDate()!=null){
                createCell(row, 8, "Доставлено" , style);
                createCell(row, 9, parcel.getDeliveryDate().format(formatter), style);
            } else {
                createCell(row, 8, "В дороге" , style);
                createCell(row, 9, "", style);
            }
            if(parcel.getParentNumber()!=null) {
                createCell(row, 15, parcel.getParentNumber(), style);
            }
            if(parcel.getPayment()!=null){
                createCell(row, 16, parcel.getPayment(), style);
            }
            if(parcel.getNote()!=null) {
                createCell(row, 17, parcel.getNote(), style);
            }

            rowNumber++;
            if(parcel.getParcelPoints()!=null){
                List<ParcelPoint> points = parcel.getParcelPoints();
                Collections.sort(points, new Comparator<ParcelPoint>(){
                    public int compare(ParcelPoint pp1, ParcelPoint pp2){
                        return pp1.getId().compareTo(pp2.getId());
                    }
                });
                for(ParcelPoint point: points){
                    row = sheet.createRow(rowNumber);
                    createCell(row, 10, point.getDepartment().getBranch().getBranchName() + ", " + point.getDepartment().getDepartmentName(), style);
                    if(point.getArriveTime()!=null) {
                        createCell(row, 11, point.getArriveTime().format(formatter), style);
                    }
                    if(point.getIntoUser()!=null) {
                        createCell(row, 12, point.getIntoUser().getUserSurname() + " " + point.getIntoUser().getUserFirstname(), style);
                    }
                    if(point.getSendTime()!=null) {
                        createCell(row, 13, point.getSendTime().format(formatter), style);
                    }
                    if(point.getOutUser()!=null) {
                        createCell(row, 14, point.getOutUser().getUserSurname() + " " + point.getOutUser().getUserFirstname(), style);
                    }
                    if(point.getParent()!=null) {
                        createCell(row, 15, point.getParent(), style);
                    }
                    if(point.getPayment()!=null) {
                        createCell(row, 16, point.getPayment(), style);
                    }
                    if(point.getText()!=null) {
                        createCell(row, 17, point.getText(), style);
                    }
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
