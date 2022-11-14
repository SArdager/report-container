package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.*;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.excelExport.JournalExcelExporter;
import kz.kdlolymp.termocontainers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

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
public class LoadDataController {

    @Autowired
    private TimeStandardService standardService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private UserService userService;
    @Autowired
    private ContainerNoteService containerNoteService;
    @Autowired
    private ContainerService containerService;

    private Gson gson = new Gson();
    private String message = "";

    @PostMapping("/user/load-data/container-notes")
    public void loadContainerNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User user = getUserFromAuthentication();
        List<ContainerNote> notes = containerNoteService.findSentContainerToDepartment(user.getDepartmentId());
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ContainerNote.class, new SentContainerNoteSerializer());
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(builder.create().toJson(notes));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/journal-export-excel")
    public void exportExcelNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("exportDepartmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        String departmentName = department.getDepartmentName() + ", " + department.getBranch().getBranchName();
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];

        DateTimeFormatter rightFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String startDateString = startDateTime.format(rightFormatter);
        String endDateString = endDateTime.format(rightFormatter);
        List<ContainerNote> notes = containerNoteService.getNotesForExportExcel(departmentId, startDateTime, endDateTime);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDateString = currentDate.format(formatter);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=notes_" + currentDateString + ".xlsx";
        resp.setContentType("application/octet-stream");
        resp.setHeader(headerKey, headerValue);
        JournalExcelExporter excelExporter = new JournalExcelExporter(notes, departmentName, startDateString, endDateString);
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

    @PostMapping("/user/load-data/journal-totalNotes")
    public void loadTotalNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 1;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        long count = containerNoteService.getAllNotesNumberByDatesAndDepartmentId(departmentId, startDateTime, endDateTime);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/journal-notes")
    public void loadJournalNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        List<ContainerNote> notes;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        if(startDateTime!=null){
            notes = containerNoteService.getNotesByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, pageable);
//        } else {
//            notes = containerNoteService.getNotesByDepartmentId(departmentId, pageable);
//        }
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

    @PostMapping("/user/load-data/time-standard")
    public void loadTimeStandard(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int firstPointId = Integer.parseInt(req.getParameter("firstPointId"));
        int secondPointId = Integer.parseInt(req.getParameter("secondPointId"));
        TimeStandard standard = standardService.findByParameters(firstPointId, secondPointId);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(standard));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/department")
    public void loadDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        Branch branch = department.getBranch();
        Branch branchFromDb = branchService.findBranchById(branch.getId());

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Branch.class, new BranchSerializer());
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(builder.create().toJson(branchFromDb));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/user")
    public void loadUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Long id = Long.parseLong(req.getParameter("id"));
        User user = userService.findUserById(id);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(User.class, new UserSerializer());
        resp.getWriter().print(builder.create().toJson(user));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/user-rights")
    public void loadUserRights(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User user = new User();
        if(req.getParameter("username")!=null && req.getParameter("username").length()>0) {
            String username = req.getParameter("username");
            user = userService.findByUsername(username);
        }
        if(user==null && req.getParameter("userId").length()>0){
            Long userId = Long.parseLong(req.getParameter("userId"));
            user = userService.findUserById(userId);
        }
        List<UserRights> userRightsList = new ArrayList<>();
        if(user!=null) {
            userRightsList = user.getUserRightsList();
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(UserRights.class, new UserRightsSerializer());
        resp.getWriter().print(builder.create().toJson(userRightsList));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/note")
    public void loadNote(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Long noteId = Long.parseLong(req.getParameter("noteId"));
        ContainerNote note = containerNoteService.findContainerNoteById(noteId);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(ContainerNote.class, new ContainerNoteSerializer())
                .registerTypeAdapter(BetweenPoint.class, new BetweenPointSerializer())
                .create();
        resp.getWriter().print(gson.toJson(note));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/container")
    public void loadContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int containerId = Integer.parseInt(req.getParameter("containerId"));
        Container container = containerService.findContainerById(containerId);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Container.class, new ContainerSerializer())
                .create();
        resp.getWriter().print(gson.toJson(container));
        resp.getWriter().flush();
        resp.getWriter().close();
    }
}
