package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.BetweenPointSerializer;
import kz.kdlolymp.termocontainers.controller.serializers.ContainerNoteSerializer;
import kz.kdlolymp.termocontainers.controller.serializers.SentContainerNoteSerializer;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.excelExport.DelayExcelExporter;
import kz.kdlolymp.termocontainers.excelExport.JournalExcelExporter;
import kz.kdlolymp.termocontainers.excelExport.PayExcelExporter;
import kz.kdlolymp.termocontainers.excelExport.RouteExcelExporter;
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
import java.util.ArrayList;
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

    private Gson gson = new Gson();
    private String message ="";

    @RequestMapping("/control/start-page")
    public String viewControlStarter(Model model){
        User user = getUserFromAuthentication();
        model.addAttribute("user", user);
        return "control/start-page";
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

    @RequestMapping("/control/pay")
    public String viewPayControl(Model model){
        User user = getUserFromAuthentication();
        int departmentId = user.getDepartmentId();
        List<UserRights> userRightsList = user.getUserRightsList();
        UserRights userRights = chooseNameRights(departmentId, userRightsList);
        Department department = departmentService.findDepartmentById(departmentId);
        List<Branch> branches = branchService.findAllBySorted();
        model.addAttribute("user", user);
        model.addAttribute("department", department);
        model.addAttribute("userRights", userRights);
        model.addAttribute("branches", branches);
        return "control/pay";
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
        } else {
            rights ="УЧЕТ ТЕРМОКОНТЕЙНЕРОВ";
        }
        userRights.setRights(rights);
        return userRights;
    }

    @RequestMapping("/control/delay")
    public String viewDelayControl(Model model){
        User user = getUserFromAuthentication();
        int departmentId = user.getDepartmentId();
        List<UserRights> userRightsList = user.getUserRightsList();
        UserRights userRights = chooseNameRights(departmentId, userRightsList);
        Department department = departmentService.findDepartmentById(departmentId);
        List<Branch> branches = branchService.findAllBySorted();
        model.addAttribute("user", user);
        model.addAttribute("department", department);
        model.addAttribute("userRights", userRights);
        model.addAttribute("branches", branches);
        return "control/delay";
    }

    @RequestMapping("/control/route")
    public String viewRouteControl(Model model){
        User user = getUserFromAuthentication();
        List<Container> containers = containerService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("containers", containers);
        return "control/route";
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
        String headerValue = "attachment; filename=notes_" + currentDateString + ".xlsx";
        resp.setContentType("application/octet-stream");
        resp.setHeader(headerKey, headerValue);
        RouteExcelExporter excelExporter = new RouteExcelExporter(notes, container, startDateString, endDateString);
        excelExporter.export(resp);
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

    @PostMapping("/control/route/report-totalNotes")
    public void loadRouteTotalNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int containerId = Integer.parseInt(req.getParameter("containerId"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        long count = containerNoteService.getAllNumberByDatesAndContainerId(containerId, startDateTime, endDateTime);

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

    @PostMapping("/control/payment/report-exportExcel")
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
        String headerValue = "attachment; filename=notes_" + currentDateString + ".xlsx";
        resp.setContentType("application/octet-stream");
        resp.setHeader(headerKey, headerValue);
        PayExcelExporter excelExporter = new PayExcelExporter(notes, departmentName, startDateString, endDateString);
        excelExporter.export(resp);
    }

    @PostMapping("/control/payment/report-totalNotes")
    public void loadPaymentTotalNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 0;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        long count = 0;
        if(departmentId>0) {
            count = containerNoteService.getAllPayNumberByDatesAndDepartmentId(departmentId, startDateTime, endDateTime);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count + ""));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/payment/report-notes")
    public void loadPaymentNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<ContainerNote> notes = containerNoteService.getPaymentNotesByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, pageable);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(ContainerNote.class, new SentContainerNoteSerializer())
                .create();
        resp.getWriter().print(gson.toJson(notes));
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
        String headerValue = "attachment; filename=notes_" + currentDateString + ".xlsx";
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
        Long delayLimit = Long.parseLong(req.getParameter("delayLimit"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        long count = 0;
        if(departmentId >0) {
            count = containerNoteService.getAllDelayNumberByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, delayLimit);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count + ""));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/control/delay/report-notes")
    public void loadDelayNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Long delayLimit = Long.parseLong(req.getParameter("delayLimit"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<ContainerNote> notes = containerNoteService.getDelayNotesByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, pageable, delayLimit);
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
