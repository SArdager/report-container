package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.*;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.excelExport.*;
import kz.kdlolymp.termocontainers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class ReportController {

    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private ContainerNoteService containerNoteService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private ParcelService parcelService;

    @Autowired
    private ParcelPointService pointService;

    private Gson gson = new Gson();
    private String message ="";

    @RequestMapping("/control/start-page")
    public String viewControlStarter(Model model){
        User user = getUserFromAuthentication();
        if (user!=null) {
            model.addAttribute("user", user);
            return "control/start-page";
        } else {
            return "redirect: ../login";
        }
    }
    private User getUserFromAuthentication() {
        String username = "";
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userService.findByUsername(username);
    }

    @RequestMapping("/control/edit-time-standard")
    public  String viewEditTimeStandard(Model model){
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            Branch userBranch = departmentService.findDepartmentById(departmentId).getBranch();
            List<Branch> branches = branchService.findAllLaborSorted();
            model.addAttribute("user", user);
            model.addAttribute("userBranch", userBranch);
            model.addAttribute("branches", branches);
            return "control/edit-time-standard";
        } else {
            return "redirect: ../login";
        }
    }
    @RequestMapping("/control/add-rights")
    public String addRightsEditor(Model model){
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            String rights ="";
            List<UserRights> userRightsList = user.getUserRightsList();
            for(UserRights uRights: userRightsList){
                if(uRights.getDepartment().getId()==departmentId){
                    rights = uRights.getRights();
                }
            }
            if(rights.equals("righter") || rights.equals("chef")) {
                List<User> users = userService.getUsersByBranchId(user.getBranchId());
                List<Department> departments = departmentService.findAllByBranchId(user.getBranchId());
                Department department = departmentService.findDepartmentById(departmentId);
                UserRights userRights = chooseNameRights(departmentId, userRightsList);
                model.addAttribute("user", user);
                model.addAttribute("department", department);
                model.addAttribute("userRights", userRights);
                model.addAttribute("users", users);
                model.addAttribute("departments", departments);
                return "control/add-rights";
            } else {
                return "redirect: ../work-starter";
            }
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/control/payment")
    public String viewPayControl(Model model){
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            Department department = departmentService.findDepartmentById(departmentId);
            List<Branch> branches = branchService.findAllLaborSorted();
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("userRights", userRights);
            model.addAttribute("branches", branches);
            return "control/payment";
        } else {
            return "redirect: ../login";
        }
    }
    private UserRights chooseNameRights(int departmentId, List<UserRights> userRightsList) {
        UserRights userRights = new UserRights();
        String rights = "";
        for(int i=0; i<userRightsList.size();i++){
            userRights = userRightsList.get(i);
            if(userRights.getDepartment().getId()==departmentId){
                rights = userRights.getRights();
            }
        }
        if(rights.equals("editor")){
            rights ="ВНЕСЕНИЕ ЗАПИСЕЙ";
        } else if(rights.equals("reader")){
            rights ="ПРОСМОТР ЗАПИСЕЙ";
        } else if(rights.equals("courier")){
            rights ="ДОСТАВКА";
        } else if(rights.equals("changer")){
            rights ="ПРОСМОТР ЖУРНАЛОВ И ИЗМЕНЕНИЕ СРОКОВ ДОСТАВКИ";
        } else if(rights.equals("righter")){
            rights ="ПРОСМОТР ЖУРНАЛОВ И ИЗМЕНЕНИЕ ПРАВ";
        } else if(rights.equals("chef")){
            rights ="ПОЛНЫЕ ПРАВА ПО ЛАБОРАТОРИИ";
        } else if(rights.equals("creator")){
            rights ="СОЗДАНИЕ И ОТСЛЕЖИВАНИЕ ПОСЫЛОК";
        } else {
            rights ="УЧЕТ ТЕРМОКОНТЕЙНЕРОВ";
        }
        userRights.setRights(rights);
        return userRights;
    }

    @RequestMapping("/control/delay")
    public String viewDelayControl(Model model){
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            Department department = departmentService.findDepartmentById(departmentId);
            List<Branch> branches = branchService.findAllLaborSorted();
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("userRights", userRights);
            model.addAttribute("branches", branches);
            return "control/delay";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/control/route")
    public String viewRouteControl(Model model){
        User user = getUserFromAuthentication();
        if (user!=null) {
            model.addAttribute("user", user);
            return "control/route";
        } else {
            return "redirect: ../login";
        }
    }

    private LocalDateTime[] getLocalDateTime(String fromDate, String untilDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);;
        LocalDateTime startDateTime;
        if(untilDate.length()>0){
            LocalDate endDate = LocalDate.parse(untilDate, formatter);
            LocalTime endTime = LocalTime.of(23, 59);
            endDateTime = LocalDateTime.of(endDate,endTime);
        }
        if(endDateTime.isAfter(LocalDateTime.now())){
            endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        }
        if(fromDate.length()>0){
            LocalDate startDate = LocalDate.parse(fromDate, formatter);
            LocalTime startTime = LocalTime.of(0, 0);
            startDateTime = LocalDateTime.of(startDate, startTime);
        } else {
            startDateTime = endDateTime.minusMonths(1);
        }
        LocalDateTime[] localDateTimes = new LocalDateTime[2];
        localDateTimes[0] = startDateTime;
        localDateTimes[1] = endDateTime;
        return localDateTimes;
    }


    @PostMapping("/control/route/report-exportExcel")
    public void exportRouteNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int containerId = Integer.parseInt(req.getParameter("containerId"));
        Container container = containerService.findContainerById(containerId);
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter rightFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String startDateString = startDateTime.format(rightFormatter);
        String endDateString = endDateTime.format(rightFormatter);
        List<ContainerNote> notes = containerNoteService.getRouteForExportExcel(containerId, startDateTime, endDateTime);
        LocalDate currentDate = LocalDate.now();
        String currentDateString = currentDate.format(formatter);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=containers_" + currentDateString + ".xlsx";
        resp.setContentType("application/octet-stream");
        resp.setHeader(headerKey, headerValue);
        RouteExcelExporter excelExporter = new RouteExcelExporter(notes, container, startDateString, endDateString);
        excelExporter.export(resp);
    }

    @PostMapping("/control/route/report-totalNotes")
    public void loadRouteTotalNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int containerId = Integer.parseInt(req.getParameter("containerId"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        int count = containerNoteService.getAllNumberByDatesAndContainerId(containerId, startDateTime, endDateTime);

        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count + ""));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/route/report-notes")
    public void loadRouteNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int containerId = Integer.parseInt(req.getParameter("containerId"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<ContainerNote> notes = containerNoteService.getNotesByDatesAndContainerId(containerId, startDateTime, endDateTime, pageable);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(ContainerNote.class, new SentContainerNoteSerializer())
                .create();
        resp.getWriter().print(gson.toJson(notes));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/payment/containers-exportExcel")
    public void exportPaymentNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        String departmentName = department.getDepartmentName() + ", " + department.getBranch().getBranchName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter rightFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        String startDateString = startDateTime.format(rightFormatter);
        String endDateString = endDateTime.format(rightFormatter);
        List<ContainerNote> notes = containerNoteService.getPaymentForExportExcel(departmentId, startDateTime, endDateTime);
        LocalDate currentDate = LocalDate.now();
        String currentDateString = currentDate.format(formatter);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=payment_" + currentDateString + ".xlsx";
        resp.setContentType("application/octet-stream");
        resp.setHeader(headerKey, headerValue);
        PayExcelExporter excelExporter = new PayExcelExporter(notes, departmentName, startDateString, endDateString);
        excelExporter.export(resp);
    }

    @PostMapping("/control/payment/report-totalNotes")
    public void loadPaymentTotalNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 1;
        int branchId = 1;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        if(req.getParameter("branchId").length()>0) {
            branchId = Integer.parseInt(req.getParameter("branchId"));
        }
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        int count = 0;
        if(branchId == 1){
            count = containerNoteService.getAllPayNumberByDatesAndBranchId(branchId, startDateTime, endDateTime);
        } else {
            if (departmentId > 1) {
                count = containerNoteService.getAllPayNumberByDatesAndDepartmentId(departmentId, startDateTime, endDateTime);
            } else {
                count = containerNoteService.getAllPayNumberByDatesAndBranchId(branchId, startDateTime, endDateTime);
            }
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/payment/report-notes")
    public void loadPaymentNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<ContainerNote> notes;
        if(branchId == 1){
            notes = containerNoteService.getPaymentNotesByDatesAndBranchId(branchId, startDateTime, endDateTime, pageable);
        } else {
            if (departmentId > 1) {
                notes = containerNoteService.getPaymentNotesByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, pageable);
            } else {
                notes = containerNoteService.getPaymentNotesByDatesAndBranchId(branchId, startDateTime, endDateTime, pageable);
            }
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(ContainerNote.class, new SentContainerNoteSerializer())
                .create();
        resp.getWriter().print(gson.toJson(notes));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/payment/parcels-exportExcel")
    public void exportParcelsPayments(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        String departmentName = department.getDepartmentName() + ", " + department.getBranch().getBranchName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter rightFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        String startDateString = startDateTime.format(rightFormatter);
        String endDateString = endDateTime.format(rightFormatter);
        List<Parcel> parcels = parcelService.getSendParcelsForExcel(departmentId, startDateTime, endDateTime);
        List<ParcelPoint> points = pointService.getPointParcelsForExcel(departmentId, startDateTime, endDateTime);
        LocalDate currentDate = LocalDate.now();
        String currentDateString = currentDate.format(formatter);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=parcels_payment_" + currentDateString + ".xlsx";
        resp.setContentType("application/octet-stream");
        resp.setHeader(headerKey, headerValue);
        ParcelsPayExcelExporter excelExporter = new ParcelsPayExcelExporter(parcels, points, departmentName, startDateString, endDateString, departmentId);
        excelExporter.export(resp);
    }

    @PostMapping("/control/payment/parcels-totalNumbers")
    public void loadTotalSendParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        int count = 0;
        if(departmentId>0){
            if(departmentId>1) {
                count = pointService.getSendParcelsNumberByDepartment(departmentId, startDateTime, endDateTime);
            } else {
                count = pointService.getAllPayingSendParcelsNumber(startDateTime, endDateTime);
            }
        } else{
            count = pointService.getSendParcelsNumberByBranch(branchId, startDateTime, endDateTime);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/payment/send-all-parcels")
    public void loadPayingSendParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<ParcelPoint> points = pointService.getPayingSendParcels(startDateTime, endDateTime, pageable);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(ParcelPoint.class, new SendParcelPointSerializer())
                .create();
        resp.getWriter().print(gson.toJson(points));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/payment/send-parcels")
    public void loadSendParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<ParcelPoint> points;
        if(departmentId>1){
            points = pointService.getSendParcelsByDepartment(departmentId, startDateTime, endDateTime, pageable);
        } else {
            points = pointService.getSendParcelsByBranch(branchId, startDateTime, endDateTime, pageable);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(ParcelPoint.class, new SendParcelPointSerializer())
                .create();
        resp.getWriter().print(gson.toJson(points));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/delay/report-exportExcel")
    public void exportDelayNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Long delayLimit = Long.parseLong(req.getParameter("delayLimit"));
        Department department = departmentService.findDepartmentById(departmentId);
        String departmentName = department.getDepartmentName() + ", " + department.getBranch().getBranchName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter rightFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        String startDateString = startDateTime.format(rightFormatter);
        String endDateString = endDateTime.format(rightFormatter);
        String delayString = delayLimit + " часов";
        List<ContainerNote> notes = containerNoteService.getDelayForExportExcel(departmentId, startDateTime, endDateTime, delayLimit);
        LocalDate currentDate = LocalDate.now();
        String currentDateString = currentDate.format(formatter);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=delay_" + currentDateString + ".xlsx";
        resp.setContentType("application/octet-stream");
        resp.setHeader(headerKey, headerValue);
        DelayExcelExporter excelExporter = new DelayExcelExporter(notes, departmentName, startDateString, endDateString, delayString);
        excelExporter.export(resp);
    }

    @PostMapping("/control/delay/report-totalNotes")
    public void loadDelayTotalNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 0;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        String way = req.getParameter("way");
        Long delayLimit = Long.parseLong(req.getParameter("delayLimit"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        int count = 0;
        if(departmentId >0) {
            count = containerNoteService.getAllDelayNumberByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, delayLimit, way);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/delay/report-notes")
    public void loadDelayNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Long delayLimit = Long.parseLong(req.getParameter("delayLimit"));
        String way = req.getParameter("way");
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<ContainerNote> notes = containerNoteService.getDelayNotesByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, pageable, delayLimit, way);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(ContainerNote.class, new ContainerNoteSerializer())
                .registerTypeAdapter(BetweenPoint.class, new BetweenPointSerializer())
                .create();
        resp.getWriter().print(gson.toJson(notes));
        resp.getWriter().flush();
        resp.getWriter().close();
    }



}
