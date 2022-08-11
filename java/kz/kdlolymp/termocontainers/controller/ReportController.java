package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.BetweenPointSerializer;
import kz.kdlolymp.termocontainers.controller.serializers.ContainerNoteSerializer;
import kz.kdlolymp.termocontainers.controller.serializers.SentContainerNoteSerializer;
import kz.kdlolymp.termocontainers.entity.*;
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

    private Gson gson = new Gson();
    private String message ="";

    @RequestMapping("/control/start-page")
    public String viewControlStarter(Model model){
        User user = getUserFromAuthentication();
        model.addAttribute("user", user);
        return "/control/start-page";
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
        return "/control/pay";
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
        return "/control/delay";
    }
    @RequestMapping("/control/route")
    public String viewRouteControl(Model model){
        User user = getUserFromAuthentication();
        int departmentId = user.getDepartmentId();
        List<UserRights> userRightsList = user.getUserRightsList();
        UserRights userRights = chooseNameRights(departmentId, userRightsList);
        Department department = departmentService.findDepartmentById(departmentId);
        List<Container> containers = containerService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("containers", containers);
        return "/control/route";
    }

    @PostMapping("/control/route/report-totalNotes")
    public void loadRouteTotalNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int containerId = Integer.parseInt(req.getParameter("containerId"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if(req.getParameter("startDate").length()>0){
            LocalDate startDate = LocalDate.parse(req.getParameter("startDate"), formatter);
            LocalTime startTime = LocalTime.of(0, 0);
            startDateTime = LocalDateTime.of(startDate, startTime);
        }
        if(req.getParameter("endDate").length()>0){
            LocalDate endDate = LocalDate.parse(req.getParameter("endDate"), formatter);
            LocalTime endTime = LocalTime.of(23, 59);
            endDateTime = LocalDateTime.of(endDate, endTime);
            if(endDateTime.isAfter(LocalDateTime.now())){
                endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
        }
        long count = 0;
        if (startDateTime != null) {
            count = containerNoteService.getAllNumberByDatesAndContainerId(containerId, startDateTime, endDateTime);
        } else {
            count = containerNoteService.getAllNumberByContainerId(containerId);
        }

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if(req.getParameter("startDate").length()>0){
            LocalDate startDate = LocalDate.parse(req.getParameter("startDate"), formatter);
            LocalTime startTime = LocalTime.of(0, 0);
            startDateTime = LocalDateTime.of(startDate, startTime);
        }
        if(req.getParameter("endDate").length()>0){
            LocalDate endDate = LocalDate.parse(req.getParameter("endDate"), formatter);
            LocalTime endTime = LocalTime.of(23, 59);
            endDateTime = LocalDateTime.of(endDate, endTime);
            if(endDateTime.isAfter(LocalDateTime.now())){
                endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
        }
        List<ContainerNote> notes;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if(startDateTime!=null){
            notes = containerNoteService.getNotesByDatesAndContainerId(containerId, startDateTime, endDateTime, pageable);
        } else {
            notes = containerNoteService.getNotesByContainerId(containerId, pageable);
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
    @PostMapping("/control/payment/report-totalNotes")
    public void loadPaymentTotalNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 0;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if(req.getParameter("startDate").length()>0){
            LocalDate startDate = LocalDate.parse(req.getParameter("startDate"), formatter);
            LocalTime startTime = LocalTime.of(0, 0);
            startDateTime = LocalDateTime.of(startDate, startTime);
        }
        if(req.getParameter("endDate").length()>0){
            LocalDate endDate = LocalDate.parse(req.getParameter("endDate"), formatter);
            LocalTime endTime = LocalTime.of(23, 59);
            endDateTime = LocalDateTime.of(endDate, endTime);
            if(endDateTime.isAfter(LocalDateTime.now())){
                endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
        }
        long count = 0;
        if(departmentId>0) {
            if (startDateTime != null) {
                count = containerNoteService.getAllPayNumberByDatesAndDepartmentId(departmentId, startDateTime, endDateTime);
            } else {
                count = containerNoteService.getAllPayNumberByDepartmentId(departmentId);
            }
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if(req.getParameter("startDate").length()>0){
            LocalDate startDate = LocalDate.parse(req.getParameter("startDate"), formatter);
            LocalTime startTime = LocalTime.of(0, 0);
            startDateTime = LocalDateTime.of(startDate, startTime);
        }
        if(req.getParameter("endDate").length()>0){
            LocalDate endDate = LocalDate.parse(req.getParameter("endDate"), formatter);
            LocalTime endTime = LocalTime.of(23, 59);
            endDateTime = LocalDateTime.of(endDate, endTime);
            if(endDateTime.isAfter(LocalDateTime.now())){
                endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
        }
        List<ContainerNote> notes;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if(startDateTime!=null){
            notes = containerNoteService.getPaymentNotesByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, pageable);
        } else {
            notes = containerNoteService.findPaymentNotesByDepartmentId(departmentId, pageable);
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
    @PostMapping("/control/delay/report-totalNotes")
    public void loadDelayTotalNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 0;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        Long delayLimit = Long.parseLong(req.getParameter("delayLimit"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if(req.getParameter("startDate").length()>0){
            LocalDate startDate = LocalDate.parse(req.getParameter("startDate"), formatter);
            LocalTime startTime = LocalTime.of(0, 0);
            startDateTime = LocalDateTime.of(startDate, startTime);
        }
        if(req.getParameter("endDate").length()>0){
            LocalDate endDate = LocalDate.parse(req.getParameter("endDate"), formatter);
            LocalTime endTime = LocalTime.of(23, 59);
            endDateTime = LocalDateTime.of(endDate, endTime);
            if(endDateTime.isAfter(LocalDateTime.now())){
                endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
        }
        long count = 0;
        if(departmentId >0) {
            if (startDateTime != null) {
                count = containerNoteService.getAllDelayNumberByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, delayLimit);
            } else {
                count = containerNoteService.getAllDelayNumberByDepartmentId(departmentId, delayLimit);
            }
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if(req.getParameter("startDate").length()>0){
            LocalDate startDate = LocalDate.parse(req.getParameter("startDate"), formatter);
            LocalTime startTime = LocalTime.of(0, 0);
            startDateTime = LocalDateTime.of(startDate, startTime);
        }
        if(req.getParameter("endDate").length()>0){
            LocalDate endDate = LocalDate.parse(req.getParameter("endDate"), formatter);
            LocalTime endTime = LocalTime.of(23, 59);
            endDateTime = LocalDateTime.of(endDate, endTime);
            if(endDateTime.isAfter(LocalDateTime.now())){
                endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
        }
        List<ContainerNote> notes;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if(startDateTime!=null){
            notes = containerNoteService.getDelayNotesByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, pageable, delayLimit);
        } else {
            notes = containerNoteService.getDelayNotesByDepartmentId(departmentId, pageable, delayLimit);
        }
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
