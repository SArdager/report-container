package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.*;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.excelExport.JournalExcelExporter;
import kz.kdlolymp.termocontainers.excelExport.LogsExcelExporter;
import kz.kdlolymp.termocontainers.excelExport.ParcelExcelExporter;
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
    private ParcelService parcelService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private UserRightsService userRightsService;
    @Autowired
    private DefaultEmailService emailService;
    @Autowired
    private EventLogService eventLogService;

    private Gson gson = new Gson();
    private String message = "";

    @PostMapping("/user/load-data/container-notes")
    public void loadContainerNotes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User user = getUserFromAuthentication();
        if(user!=null) {
            GsonBuilder builder = new GsonBuilder();
            List<ContainerNote> notes = containerNoteService.findSentContainerToDepartment(user.getDepartmentId());
            builder.registerTypeAdapter(ContainerNote.class, new SentContainerNoteSerializer());
            resp.setContentType("json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().print(builder.create().toJson(notes));
            resp.getWriter().flush();
            resp.getWriter().close();
        }
    }
    @PostMapping("/user/load-data/parcels")
    public void loadDeliveryParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User user = getUserFromAuthentication();
        if(user!=null) {
            List<Parcel> parcels = parcelService.getMovedParcelsByDepartmentId(user.getDepartmentId());
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Parcel.class, new RouteParcelSerializer());
            resp.setContentType("json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().print(builder.create().toJson(parcels));
            resp.getWriter().flush();
            resp.getWriter().close();
        }
    }

    @PostMapping("/user/load-data/parcels-export-excel")
    public void exportExcelParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        List<Parcel> parcels = parcelService.getParcelsForExportExcel(departmentId, startDateTime, endDateTime);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDateString = currentDate.format(formatter);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=parcels_" + currentDateString + ".xlsx";
        resp.setContentType("application/octet-stream");
        resp.setHeader(headerKey, headerValue);
        ParcelExcelExporter excelExporter = new ParcelExcelExporter(parcels, departmentName, startDateString, endDateString);
        excelExporter.export(resp);
    }

    @PostMapping("/user/load-data/ready_parcels")
    public void loadReadyParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User user = getUserFromAuthentication();
        if(user!=null) {
            List<Parcel> parcels = parcelService.getWaitedParcelsByDepartmentId(user.getDepartmentId());
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Parcel.class, new RouteParcelSerializer());
            resp.setContentType("json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().print(builder.create().toJson(parcels));
            resp.getWriter().flush();
            resp.getWriter().close();
        }
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
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
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

    @PostMapping("/user/load-data/parcels-totalNumbers")
    public void loadTotalParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 1;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        int count = parcelService.getAllParcelsNumberByDatesAndDepartmentId(departmentId, startDateTime, endDateTime);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/parcels-totalOutNumbers")
    public void loadTotalOutParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 1;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        int count = 0;
        if(departmentId>1){
            count = parcelService.getAllOutParcelsNumberByDepartment(departmentId, startDateTime, endDateTime);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/parcels-totalToNumbers")
    public void loadTotalToParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 1;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        int count = 0;
        if(departmentId>1) {
            count = parcelService.getAllToParcelsNumberByDepartment(departmentId, startDateTime, endDateTime);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/all-parcels")
    public void loadAllParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Parcel> parcels = parcelService.getParcelsByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, pageable);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Parcel.class, new ParcelSerializer())
                .registerTypeAdapter(ParcelPoint.class, new ParcelPointSerializer())
                .create();
        resp.getWriter().print(gson.toJson(parcels));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/out-parcels")
    public void loadOutParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Parcel> parcels = parcelService.getOutParcelsByDepartment(departmentId, startDateTime, endDateTime, pageable);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Parcel.class, new ParcelSerializer())
                .registerTypeAdapter(ParcelPoint.class, new ParcelPointSerializer())
                .create();
        resp.getWriter().print(gson.toJson(parcels));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/to-parcels")
    public void loadToParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Parcel> parcels = parcelService.getToParcelsByDepartment(departmentId, startDateTime, endDateTime, pageable);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Parcel.class, new ParcelSerializer())
                .registerTypeAdapter(ParcelPoint.class, new ParcelPointSerializer())
                .create();
        resp.getWriter().print(gson.toJson(parcels));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/parcels-at-route")
    public void loadParcelsAtRoute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 1;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        List<Parcel> parcels = parcelService.getAllParcelsOnRoute(departmentId);
        gson = new GsonBuilder()
                .registerTypeAdapter(Parcel.class, new ParcelSerializer())
                .registerTypeAdapter(ParcelPoint.class, new ParcelPointSerializer())
                .create();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(gson.toJson(parcels));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/search-parcel")
    public void searchParcelByNumber(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("UTF-8");
        String findNumber = req.getParameter("findNumber");
        Parcel parcel = parcelService.searchParcelByNumber(findNumber);
        gson = new GsonBuilder()
                .registerTypeAdapter(Parcel.class, new ParcelSerializer())
                .registerTypeAdapter(ParcelPoint.class, new ParcelPointSerializer())
                .create();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(gson.toJson(parcel));
        resp.getWriter().flush();
        resp.getWriter().close();
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
        int count = containerNoteService.getAllNotesNumberByDatesAndDepartmentId(departmentId, startDateTime, endDateTime);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/journal-at-route")
    public void loadContainersAtRoute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = 1;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        List<ContainerNote> notes = containerNoteService.getAllNotesOnRoute(departmentId);
        gson = new GsonBuilder()
                .registerTypeAdapter(ContainerNote.class, new ContainerNoteSerializer())
                .registerTypeAdapter(BetweenPoint.class, new BetweenPointSerializer())
                .create();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(gson.toJson(notes));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/journal-at-home")
    public void loadContainersAtHome(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId=0;
        if(req.getParameter("departmentId").length()>0) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        Department department = departmentService.findDepartmentById(departmentId);
        List<ContainerNote> notes = new ArrayList<>();
        List<Container> containers = containerService.findAllByDepartmentId(department.getId());
        if(containers!=null && containers.size()>0){
            int[] index = new int[containers.size()];
            for(int i=0; i<containers.size(); i++){
                index[i] = containers.get(i).getId();
            }
            notes = containerNoteService.getAllNotesAtHome(index);
        }
        gson = new GsonBuilder()
                .registerTypeAdapter(ContainerNote.class, new ContainerNoteSerializer())
                .registerTypeAdapter(BetweenPoint.class, new BetweenPointSerializer())
                .create();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(gson.toJson(notes));
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
        notes = containerNoteService.getNotesByDatesAndDepartmentId(departmentId, startDateTime, endDateTime, pageable);

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

    @PostMapping("/user/load-data/edit-standard")
    public void editTimeStandard(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int standardId = 0;
        if (req.getParameter("standardId").length() > 0) {
            standardId = Integer.parseInt(req.getParameter("standardId"));
        }
        int firstPointId = Integer.parseInt(req.getParameter("firstPointId"));
        int secondPointId = Integer.parseInt(req.getParameter("secondPointId"));
        int timeStandard = Integer.parseInt(req.getParameter("timeStandard"));
        int oldValue = 0;
        TimeStandard standard = new TimeStandard();
        standard.setTimeStandard(timeStandard);
        standard.setFirstPointId(firstPointId);
        standard.setSecondPointId(secondPointId);
        User userEditor = getUserFromAuthentication();
        if (standardId > 0) {
            standard.setId(standardId);
            TimeStandard oldStandard = standardService.findById(standardId);
            oldValue = oldStandard.getTimeStandard();
        }
        if (standardService.save(standard)) {
            Long secondLongId = Long.valueOf(secondPointId);
            eventLogService.saveEvent(userEditor, firstPointId, secondLongId, timeStandard + "", oldValue + "", 1);
            if (standardId > 0) {
                message = "Стандарт времени доставки  изменен";
            } else {
                message = "Новый стандарт времени доставки добавлен";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/department")
    public void loadDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        Branch branch = department.getBranch();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Branch.class, new BranchSerializer());
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(builder.create().toJson(branch));
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
        if(req.getParameter("username")!=null && req.getParameter("username").length()>0){
            String username = req.getParameter("username");
            user = userService.findByUsername(username);
        }
        if(user == null || user.getId() == null) {
            if(req.getParameter("userId")!=null && Long.parseLong(req.getParameter("userId"))>0) {
                Long userId = Long.parseLong(req.getParameter("userId"));
                user = userService.findUserById(userId);
            }
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

    @PostMapping("/user/change-rights")
    public  void changeRights(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User user = new User();
        if(req.getParameter("userId")!=null && Long.parseLong(req.getParameter("userId"))>0) {
            Long userId = Long.parseLong(req.getParameter("userId"));
            user = userService.findUserById(userId);
        }
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        String rights = req.getParameter("rights");
        boolean isRightsNeedAdd = true;
        String oldValue = "reset";

        List<UserRights> userRightsList = user.getUserRightsList();
        if(userRightsList!=null && userRightsList.size()>0) {
            for (int i = 0; i < userRightsList.size(); i++) {
                UserRights userRights = userRightsList.get(i);
                int currentDepartmentId = userRights.getDepartment().getId();
                if (currentDepartmentId == departmentId) {
                    isRightsNeedAdd = false;
                    oldValue = userRights.getRights();
                    if (!rights.equals("reset")) {
                        userRights.setRights(rights);
                        userRightsService.changeUserRights(userRights);
                    } else {
                        userRightsList.remove(i);
                        userRightsService.deleteUserRights(userRights);
                    }
                    user.setUserRightsList(userRightsList);
                }
            }
        }
        if(isRightsNeedAdd && !rights.equals("reset")){
            UserRights newUserRights = new UserRights();
            newUserRights.setDepartment(department);
            newUserRights.setRights(rights);
            newUserRights.setUser(user);
            UserRights userRights = userRightsService.addNewUserRights(newUserRights);
            user.addUserRights(userRights);
        }
        String userRights;
        if(rights.equals("reader")){userRights = "просмотр записей и получение отчетов";}
        else if(rights.equals("editor")){userRights = "внесение и редактирование записей";}
        else if(rights.equals("courier")){userRights = "курьер - внесение записей";}
        else if(rights.equals("changer")){userRights = "просмотр записей и изменение срока доставки";}
        else if(rights.equals("righter")){userRights = "просмотр записей и редактирование прав";}
        else if(rights.equals("chef")){userRights = "полные права по лаборатории";}
        else if(rights.equals("account")){userRights = "по учету термоконтейнеров";}
        else if(rights.equals("creator")){userRights = "по созданию и отправке почтовых корреспонденций";}
        else if(rights.equals("reset")){userRights = "убраны права доступа";}
        else {userRights = "undefined";}

        if(userService.saveUser(user)){
            String userName = user.getUserSurname() + " " + user.getUserFirstname();
            String email = user.getEmail();
            if(!rights.equals("reset")){
                String departmentString = department.getDepartmentName() + ", " + department.getBranch().getBranchName();
                String finalUserRights = userRights;
                Thread sendMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        emailService.sendNewRightsMessage(userName, email, departmentString, finalUserRights);
                    }
                });
                sendMessage.start();
                message = "Пользователю " + userName + " добавлены (изменены) права. <br>" +
                            "Сообщение об изменении прав отправлено на корпоративный почтовый адрес пользователя.";
            } else {
                message = "Пользователю " + userName + " убраны права по объекту.";
            }
        } else {
            message = "Ошибка изменения прав пользователя";
        }
        User userEditor = getUserFromAuthentication();
        if(oldValue.equals("reader")){oldValue = "просмотр записей и получение отчетов";}
        else if(oldValue.equals("editor")){oldValue = "внесение и редактирование записей";}
        else if(oldValue.equals("courier")){oldValue = "курьер - внесение записей";}
        else if(oldValue.equals("changer")){oldValue = "просмотр записей и изменение срока доставки";}
        else if(oldValue.equals("righter")){oldValue = "просмотр записей и редактирование прав";}
        else if(oldValue.equals("chef")){oldValue = "полные права по лаборатории";}
        else if(oldValue.equals("account")){oldValue = "по учету термоконтейнеров";}
        else if(oldValue.equals("creator")){userRights = "по созданию и отправке почтовых корреспонденций";}
        else if(oldValue.equals("reset")){oldValue = "отсутствуют права";}
        else {oldValue = "undefined";}
        eventLogService.saveEvent(userEditor, departmentId, user.getId(), userRights, oldValue, 2);

        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/del-user")
    public  void delUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User user = new User();
        if(req.getParameter("userId")!=null && Long.parseLong(req.getParameter("userId"))>0) {
            Long userId = Long.parseLong(req.getParameter("userId"));
            user = userService.findUserById(userId);
        }
        String userName = user.getUserSurname() + " " + user.getUserFirstname();
        user.setEnabled(false);
        User userEditor = getUserFromAuthentication();
        if(userService.saveUser(user)){
            message = "Пользователю " + userName + " закрыт доступ в систему.";
            eventLogService.saveEvent(userEditor, 1, user.getId(), message, "", 2);

        } else {
            message = "Ошибка удаления пользователя: " + userName;
        }

        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
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

    @PostMapping("/user/load-data/find-notes")
    public void loadNotesByContainerNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        List<ContainerNote> notes = containerNoteService.findNotesByContainerNumber(containerNumber);
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

    @PostMapping("/user/load-data/container")
    public void loadContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        Container container = containerService.findByContainerNumber(containerNumber);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Container.class, new ContainerSerializer())
                .create();
        resp.getWriter().print(gson.toJson(container));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-data/containers")
    public void loadContainerList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        List<Container> containers = containerService.findAllByPartContainerNumber(containerNumber);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Container.class, new ContainerSerializer())
                .create();
        resp.getWriter().print(gson.toJson(containers));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/load-data/totalLogs")
    public void loadTotalLogs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int eventId = Integer.parseInt(req.getParameter("eventId"));
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        int count = eventLogService.getAllLogsNumberByDates(eventId, branchId, startDateTime, endDateTime);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(count));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/load-data/logs-journal")
    public void loadLogsJournal(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int eventId = Integer.parseInt(req.getParameter("eventId"));
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];
        List<EventLog> logs;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        logs = eventLogService.getLogsByDates(eventId, branchId, startDateTime, endDateTime, pageable);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        if(eventId==1){
            gson = new GsonBuilder()
                .registerTypeAdapter(EventLog.class, new EventLogSerializer(userService, departmentService))
                .create();
        } else {
            gson = new GsonBuilder()
                    .registerTypeAdapter(EventLog.class, new EventLogSerializer(userService, departmentService))
                    .create();
        }
        resp.getWriter().print(gson.toJson(logs));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/load-data/logs-exportExcel")
    public void exportEventLogs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int eventId = Integer.parseInt(req.getParameter("eventId"));
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        String branchName;
        if(branchId>0) {
            branchName = branchService.findBranchById(branchId).getBranchName();
        } else {
            branchName = "По всем филиалам";
        }
        LocalDateTime[] localDateTimes = getLocalDateTime(req.getParameter("startDate"), req.getParameter("endDate"));
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter rightFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String startDateString = startDateTime.format(rightFormatter);
        String endDateString = endDateTime.format(rightFormatter);
        List<EventLog> logs = eventLogService.getLogsForExportExcel(eventId, branchId, startDateTime, endDateTime);
        LocalDate currentDate = LocalDate.now();
        String currentDateString = currentDate.format(formatter);
        String headerKey = "Content-Disposition";
        String headerValue;
        if(branchId == 1){
            headerValue = "attachment; filename=deliver_time_logs_" + currentDateString + ".xlsx";
        } else {
            headerValue ="attachment; filename=rights_logs_" + currentDateString + ".xlsx";
        }
         resp.setContentType("application/octet-stream");
        resp.setHeader(headerKey, headerValue);
        LogsExcelExporter excelExporter = new LogsExcelExporter(logs, eventId, branchName, startDateString, endDateString, userService, departmentService);
        excelExporter.export(resp);
    }


}
