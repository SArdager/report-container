package kz.kdlolymp.termocontainers.excelExport;

import kz.kdlolymp.termocontainers.entity.ContainerNote;
import kz.kdlolymp.termocontainers.entity.Parcel;
import kz.kdlolymp.termocontainers.entity.ParcelPoint;
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

public class ParcelsPayExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Parcel> parcels;
    private List<ParcelPoint> points;
    private String startDate;
    private String endDate;
    private String departmentName;
    private int departmentId;

    public ParcelsPayExcelExporter(List<Parcel> parcels, List<ParcelPoint> points, String departmentName,
                                   String startDate, String endDate, int departmentId) {
        this.parcels = parcels;
        this.points = points;
        this.departmentName = departmentName;
        this.departmentId = departmentId;
        this.startDate = startDate;
        this.endDate = endDate;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine(String sheetName, String sheetTitle){
        sheet = workbook.createSheet(sheetName);
        CellStyle style = getStyle();
        Row row = sheet.createRow(0);
        createCell(row, 1, sheetTitle, style);
    }
    private void writeTitleLine(){
        sheet.setDefaultColumnWidth(14);
        CellStyle style = getStyle();
        CellStyle boldStyle = getBoldStyle();
        Row row = sheet.createRow(1);
        createCell(row, 1, "по филиалу:", style);
        createCell(row, 2, departmentName, boldStyle);
        row = sheet.createRow(2);
        createCell(row, 1, "С даты:", style);
        createCell(row, 2, startDate, boldStyle);
        row = sheet.createRow(3);
        createCell(row, 1, "На дату:", style);
        createCell(row, 2, endDate, boldStyle);
        row = sheet.createRow(4);
        createCell(row, 0, "Номер посылки", boldStyle);
        createCell(row, 1, "Вид посылки", boldStyle);
        createCell(row, 2, "Дата отгрузки", boldStyle);
        if(departmentId>1) {
            createCell(row, 3, "Вложена в", boldStyle);
            createCell(row, 4, "Отправил", boldStyle);
            createCell(row, 5, "Пункт получения", boldStyle);
            createCell(row, 6, "Оплата доставки", boldStyle);
            createCell(row, 7, "Примечание", boldStyle);
        } else {
            createCell(row, 3, "Пункт отгрузки", boldStyle);
            createCell(row, 4, "Вложена в", boldStyle);
            createCell(row, 5, "Отправил", boldStyle);
            createCell(row, 6, "Пункт получения", boldStyle);
            createCell(row, 7, "Оплата доставки", boldStyle);
            createCell(row, 8, "Примечание", boldStyle);
        }
    }

    private void writeDataLines() {
        sheet.setDefaultColumnWidth(18);
        int rowNumber = 5;
        Long total = 0L;
        CellStyle style = getStyle();
        CellStyle boldStyle = getBoldStyle();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        if(departmentId>1) {
            for (ParcelPoint point : points) {
                Row row = sheet.createRow(rowNumber);
                createCell(row, 0, point.getParcelNumber(), style);
                createCell(row, 1, getParcelType(point.getParcelNumber()), style);
                createCell(row, 2, point.getSendTime().format(formatter), style);
                createCell(row, 3, point.getParent(), style);
                createCell(row, 4, point.getOutUser().getUserFirstname().substring(0, 1) + ". " + point.getOutUser().getUserSurname(), style);
                createCell(row, 5, point.getToDepartment().getBranch().getBranchName() + ", " + point.getToDepartment().getDepartmentName(), style);
                createCell(row, 6, point.getPayment(), style);
                createCell(row, 7, point.getText(), style);
                total += point.getPayment();
                rowNumber++;
            }
            Row lastRow = sheet.createRow(rowNumber);
            createCell(lastRow, 1, "Сумма по филиалу:", boldStyle);
            createCell(lastRow, 6, total, boldStyle);
        } else {
            String branch = "";
            for (int i=0; i<points.size(); i++) {
                Row row = sheet.createRow(rowNumber);
                ParcelPoint point = points.get(i);
                if(point.getDepartment().getBranch().getBranchName()!=branch){
                    if(i>0){
                        createCell(row, 1, "Сумма по филиалу:", boldStyle);
                        createCell(row, 6, total, boldStyle);
                        rowNumber++;
                        total = 0L;
                        row = sheet.createRow(rowNumber);
                    }
                    branch = point.getDepartment().getBranch().getBranchName();
                    createCell(row, 0, branch, boldStyle);
                    rowNumber++;
                    row = sheet.createRow(rowNumber);
                }
                createCell(row, 0, point.getParcelNumber(), style);
                createCell(row, 1, getParcelType(point.getParcelNumber()), style);
                createCell(row, 2, point.getSendTime().format(formatter), style);
                createCell(row, 3, point.getDepartment().getDepartmentName(), style);
                createCell(row, 4, point.getParent(), style);
                createCell(row, 5, point.getOutUser().getUserFirstname().substring(0, 1) + ". " + point.getOutUser().getUserSurname(), style);
                createCell(row, 6, point.getToDepartment().getBranch().getBranchName() + ", " + point.getToDepartment().getDepartmentName(), style);
                createCell(row, 7, point.getPayment(), style);
                createCell(row, 8, point.getText(), style);
                total += point.getPayment();
                rowNumber++;
            }
            Row lastRow = sheet.createRow(rowNumber);
            createCell(lastRow, 1, "Сумма по филиалу:", boldStyle);
            createCell(lastRow, 7, total, boldStyle);
        }
    }

    private String getParcelType(String parcelNumber) {
        String parcelType = "";
        switch (parcelNumber.substring(0, 1)) {
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
        return parcelType;
    }

    private CellStyle getStyle() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setBold(false);
        style.setFont(font);
        return style;
    }

    private CellStyle getBoldStyle() {
        CellStyle boldStyle = workbook.createCellStyle();
        XSSFFont boldFont = workbook.createFont();
        boldFont.setFontHeight(12);
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        return boldStyle;
    }

    private void writeSecondTitleLine(){
        sheet.setDefaultColumnWidth(14);
        CellStyle style = getStyle();
        CellStyle boldStyle = getBoldStyle();
        Row row = sheet.createRow(1);
        createCell(row, 1, "по филиалу:", style);
        createCell(row, 2, departmentName, boldStyle);
        row = sheet.createRow(2);
        createCell(row, 1, "С даты:", style);
        createCell(row, 2, startDate, boldStyle);
        row = sheet.createRow(3);
        createCell(row, 1, "На дату:", style);
        createCell(row, 2, endDate, boldStyle);
        row = sheet.createRow(4);
        createCell(row, 0, "Номер посылки", boldStyle);
        createCell(row, 1, "Вид посылки", boldStyle);
        createCell(row, 2, "Габариты", boldStyle);
        createCell(row, 3, "Отправитель", boldStyle);
        createCell(row, 4, "Получатель", boldStyle);
        createCell(row, 5, "Пункт отправки", boldStyle);
        createCell(row, 6, "Пункт доставки", boldStyle);
        createCell(row, 7, "Дата отправки", boldStyle);
        createCell(row, 8, "Отправил", boldStyle);
        createCell(row, 9, "Вложено в", boldStyle);
        createCell(row, 10, "Оплата доставки", boldStyle);
        createCell(row, 11, "Примечание", boldStyle);
    }

    private void writeSecondDataLines() {
        sheet.setDefaultColumnWidth(18);
        int rowNumber = 5;
        CellStyle boldStyle = getBoldStyle();
        if(departmentId > 1) {
            for (Parcel parcel :parcels) {
                Row row = sheet.createRow(rowNumber);
                fillParcelPow(row, parcel);
                rowNumber++;
                rowNumber = addPointsRows(parcel, rowNumber);
            }
        } else {
            String branch = "";
            for (int i=0; i<parcels.size(); i++) {
                Row row = sheet.createRow(rowNumber);
                Parcel parcel = parcels.get(i);
                if(parcel.getOutDepartment().getBranch().getBranchName()!=branch){
                    branch = parcel.getOutDepartment().getBranch().getBranchName();
                    createCell(row, 0, branch, boldStyle);
                    rowNumber++;
                    row = sheet.createRow(rowNumber);
                }
                fillParcelPow(row, parcel);
                rowNumber++;
                rowNumber = addPointsRows(parcel, rowNumber);
            }
        }
    }

    private void fillParcelPow(Row row, Parcel parcel) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        CellStyle style = getStyle();
        CellStyle boldStyle = getBoldStyle();
        createCell(row, 0, parcel.getParcelNumber(), style);
        createCell(row, 1, getParcelType(parcel.getParcelNumber()), style);
        createCell(row, 2, parcel.getDimensions(), style);
        createCell(row, 3, parcel.getOutDepartment().getBranch().getBranchName() + ", " + parcel.getOutDepartment().getDepartmentName(), style);
        createCell(row, 4, parcel.getDestination().getBranch().getBranchName() + ", " + parcel.getDestination().getDepartmentName(), style);
        createCell(row, 10, parcel.getPayment(), boldStyle);
        if(parcel.getNote()!=null) {
            createCell(row, 11, parcel.getNote(), style);
        }
    }

    private int addPointsRows(Parcel parcel, int rowNumber) {
        CellStyle style = getStyle();
        CellStyle boldStyle = getBoldStyle();
        List<ParcelPoint> points = parcel.getParcelPoints();
        Collections.sort(points, new Comparator<ParcelPoint>(){
            public int compare(ParcelPoint pp1, ParcelPoint pp2){
                return pp1.getId().compareTo(pp2.getId());
            }
        });
        for(ParcelPoint point: points){
            Row row = sheet.createRow(rowNumber);
            if(departmentId>1 && point.getDepartment().getId() == departmentId){
                fillPointRow(row, boldStyle, point);
            } else {
                fillPointRow(row, style, point);
            }
            rowNumber++;
        }
        return rowNumber;
    }

    private void fillPointRow(Row row, CellStyle style, ParcelPoint point) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        createCell(row, 5, point.getDepartment().getBranch().getBranchName() + ", " + point.getDepartment().getDepartmentName(), style);
        if(point.getToDepartment()!=null) {
            createCell(row, 6, point.getToDepartment().getBranch().getBranchName() + ", " + point.getToDepartment().getDepartmentName(), style);
        }
        if(point.getSendTime()!=null) {
            createCell(row, 7, point.getSendTime().format(formatter), style);
        }
        if(point.getOutUser()!=null) {
            createCell(row, 8, point.getOutUser().getUserFirstname().substring(0, 1) + ". " + point.getOutUser().getUserSurname(), style);
        }
        if(point.getParent()!=null) {
            createCell(row, 9, point.getParent(), style);
        }
        if(point.getPayment()!=null) {
            createCell(row, 10, point.getPayment(), style);
        }
        if(point.getText()!=null) {
            createCell(row, 11, point.getText(), style);
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
        if(departmentId>0) {
            writeHeaderLine("Payers", "Отчет по плательщикам почтовых отправлений");
            writeTitleLine();
            writeDataLines();
            writeHeaderLine("Parcels", "Отчет по оплате почтовых отправлений");
            writeSecondTitleLine();
            writeSecondDataLines();
        }
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

}
