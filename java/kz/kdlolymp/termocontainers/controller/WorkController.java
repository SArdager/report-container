package kz.kdlolymp.termocontainers.controller;

import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.repositories.ContainerNoteRepository;
import kz.kdlolymp.termocontainers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class WorkController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private UserService userService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private ContainerValueService containerValueService;
    @Autowired
    private TimeStandardService timeStandardService;
    @Autowired
    private ContainerNoteService containerNoteService;
    @Autowired
    private ProbeService probeService;
    @Autowired
    private BetweenPointService betweenPointService;
    @Autowired
    private DefaultEmailService emailService;
    private String message;

    @RequestMapping("/work-starter")
    public  String viewWorkStarter(Model model){
        User user = getUserFromAuthentication();
        int departmentId = user.getDepartmentId();
        List<UserRights> userRightsList = user.getUserRightsList();
        UserRights userRights = chooseNameRights(departmentId, userRightsList);
        Department department = departmentService.findDepartmentById(departmentId);
        model.addAttribute("user", user);
        model.addAttribute("department", department);
        model.addAttribute("userRights", userRights);
        return "/work-starter";
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

    @RequestMapping("/user/check-in")
    public  String viewCheckIn(Model model){
        User user = getUserFromAuthentication();
        int departmentId = user.getDepartmentId();
        List<UserRights> userRightsList = user.getUserRightsList();
        UserRights userRights = chooseNameRights(departmentId, userRightsList);
        Department department = departmentService.findDepartmentById(departmentId);
        model.addAttribute("user", user);
        model.addAttribute("department", department);
        model.addAttribute("userRights", userRights);
        return "/user/check-in";
    }
    @RequestMapping("/user/check-between")
    public  String viewCheckBetween(Model model){
        User user = getUserFromAuthentication();
        int departmentId = user.getDepartmentId();
        List<UserRights> userRightsList = user.getUserRightsList();
        UserRights userRights = chooseNameRights(departmentId, userRightsList);
        Department department = departmentService.findDepartmentById(departmentId);
        model.addAttribute("user", user);
        model.addAttribute("department", department);
        model.addAttribute("userRights", userRights);
        return "/user/check-between";
    }

    @RequestMapping("/user/check-out")
    public  String viewCheckOut(Model model){
        User user = getUserFromAuthentication();
        int departmentId = user.getDepartmentId();
        List<UserRights> userRightsList = user.getUserRightsList();
        UserRights userRights = chooseNameRights(departmentId, userRightsList);
        Department department = departmentService.findDepartmentById(departmentId);
        List<Branch> branches = branchService.findAllBySorted();
        List<Probe> probes = probeService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("department", department);
        model.addAttribute("branches", branches);
        model.addAttribute("probes", probes);
        model.addAttribute("userRights", userRights);
        return "/user/check-out";
    }

    @RequestMapping("/user/check-journal")
    public  String viewCheckBook(Model model){
        User user = getUserFromAuthentication();
        int departmentId = user.getDepartmentId();
        List<Branch> branches = branchService.findAllBySorted();
        List<UserRights> userRightsList = user.getUserRightsList();
        UserRights userRights = chooseNameRights(departmentId, userRightsList);
        Department department = departmentService.findDepartmentById(departmentId);
        model.addAttribute("user", user);
        model.addAttribute("department", department);
        model.addAttribute("branches", branches);
        model.addAttribute("userRights", userRights);
        return "/user/check-journal";
    }

    @RequestMapping("/user/check-container")
    public  String viewCheckContainer(Model model){
        User user = getUserFromAuthentication();
        int departmentId = user.getDepartmentId();
        List<UserRights> userRightsList = user.getUserRightsList();
        UserRights userRights = chooseNameRights(departmentId, userRightsList);
        Department department = departmentService.findDepartmentById(departmentId);
        List<ContainerValue> containerValues = containerValueService.findAll();
        List<Branch> branches = branchService.findAllBySorted();
        model.addAttribute("containerValues", containerValues);
        model.addAttribute("user", user);
        model.addAttribute("department", department);
        model.addAttribute("branches", branches);
        model.addAttribute("userRights", userRights);
        return "/user/check-container";
    }
    @PostMapping("/user/check-out/send")
    public  void checkOutContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        int departmentToId = Integer.parseInt(req.getParameter("toId"));
        int probeId = Integer.parseInt(req.getParameter("probeId"));
        Long payment = Long.parseLong(req.getParameter("payment"));
        String containerNumber = req.getParameter("containerNumber");
        String text = req.getParameter("text");
        boolean isFirstSend = Boolean.parseBoolean(req.getParameter("isFirstSend"));
        Container container = containerService.findByContainerNumber(containerNumber);
        User user = getUserFromAuthentication();
        ContainerNote containerNote = new ContainerNote();

        if(container.getDepartment().getId()==user.getDepartmentId()) {
            if(containerNoteService.isContainerSend(container) && isFirstSend){
                message = "Данный термоконтейнер уже оформлен на отправку. \nИзменить место назначения доставки?";
            } else {
                String dateString = req.getParameter("date");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
                containerNote.setContainer(container);
                Department outDepartment = departmentService.findDepartmentById(user.getDepartmentId());
                Department toDepartment = departmentService.findDepartmentById(departmentToId);
                containerNote.setOutDepartment(outDepartment);
                containerNote.setToDepartment(toDepartment);
                containerNote.setOutUser(user);
                containerNote.setSendTime(dateTime);
                TimeStandard timeStandard = timeStandardService.findByParameters(user.getDepartmentId(), departmentToId, probeId);
                containerNote.setTimeStandard(timeStandard.getTimeStandard());
                containerNote.setSendTime(dateTime);
                containerNote.setSendNote(text);
                containerNote.setSendPay(payment);
                containerNote.setSend(true);
                if(!isFirstSend) {
                    containerNote.setId(containerNoteService.findSentContainer(container).getId());
                }
                if (containerNoteService.saveNote(containerNote)) {
                    ContainerNote savedNote = containerNoteService.findSentContainer(container);
                    if(isFirstSend) {
                        message = "Оформлена отгрузка термоконтейнера получателю: \n" + savedNote.getToDepartment().getDepartmentName() + ", " +
                            savedNote.getToDepartment().getBranch().getBranchName() + ".\n Номер документа отгрузки: " + savedNote.getId();
                    } else {
                        message = "Отгрузка термоконтейнера переоформлена на получателя: \n" + savedNote.getToDepartment().getDepartmentName() + ", " +
                            savedNote.getToDepartment().getBranch().getBranchName() + ".\n Номер документа отгрузки: " + savedNote.getId();
                    }
                } else {
                    message = "Ошибка оформления отгрузки. Повторите попытку";
                }
            }
        } else {
            message = "Термоконтейнер зарегистрирован на другом объекте. \nНеобходимо вначале оформить его передачу на данный объект.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }


    @PostMapping("/user/check-out/save-changes")
    public  void changesOutContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        Long noteId = Long.parseLong(req.getParameter("noteId"));
        ContainerNote note = containerNoteService.findContainerNoteById(noteId);
        boolean isOutChange = Boolean.parseBoolean(req.getParameter("isOutChange"));
        if(isOutChange) {
            int departmentToId = Integer.parseInt(req.getParameter("toDepartment"));
            Long payment = 0L;
            if (req.getParameter("changePay").length() > 0) {
                payment = Long.parseLong(req.getParameter("changePay"));
            }
            String text = req.getParameter("changeNote");
            Department toDepartment = departmentService.findDepartmentById(departmentToId);
            note.setToDepartment(toDepartment);
            if(text.length()>0) {
                note.setSendNote(text);
            }
            if(payment>0) {
                note.setSendPay(payment);
            }
        } else {
            int departmentId = Integer.parseInt(req.getParameter("departmentId"));
            String arriveText = req.getParameter("changeArriveNote");
            String betweenText = req.getParameter("changeBetweenNote");
            if(arriveText.length()>0) {
                note.setArriveNote(arriveText);
            } else{
                List<BetweenPoint> points = note.getBetweenPoints();
                for(BetweenPoint point: points){
                    if(point.getDepartment().getId()==departmentId){
                        point.setPointNote(betweenText);
                    }
                }
            }
        }
        if(containerNoteService.saveNote(note)) {
            message = "В маршрутный лист термоконтейнера внесены изменения.<br>Для просмотра изменений перезагрузите данные по перемещению термоконтейнера";
        } else {
            message = "Ошибка записи изменений. Повторите";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-between/check")
    public  void checkBetweenContainer (HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        String text = req.getParameter("text");
        User user = getUserFromAuthentication();
        String dateString = req.getParameter("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime currentDateTime = LocalDateTime.parse(dateString, formatter);
        Department betweenDepartment = departmentService.findDepartmentById(user.getDepartmentId());
        ContainerNote note = containerNoteService.findNoteByContainer(containerNumber);
        if(note.getId()!=null){
            Department toDepartment = note.getToDepartment();
            String depToId = toDepartment.getId() +"";
            String depBetweenId = betweenDepartment.getId()+"";
            if(!depToId.equals(depBetweenId)) {
                BetweenPoint point = new BetweenPoint();
                point.setContainerNoteId(note.getId());
                point.setPassUser(user);
                point.setDepartment(betweenDepartment);
                point.setPassTime(currentDateTime);
                point.setPointNote(text);
                List<BetweenPoint> points;
                boolean isChecked = false;
                if (note.getBetweenPoints() != null) {
                    points = note.getBetweenPoints();
                    for(int i=0; i<points.size(); i++){
                        if(points.get(i).getDepartment().getId()==betweenDepartment.getId()){
                            isChecked = true;
                        }
                    }
                } else {
                    points = new ArrayList<>();
                }
                if(isChecked){
                    message = "Попытка повторной регистрации. \nДля данного термоконтейнера регистрации произведена.";
                } else {
                    if (betweenPointService.addNewPoint(point)) {
                        points.add(point);
                        note.setBetweenPoints(points);
                        if(containerNoteService.saveNote(note)){
                            message = "Термоконтейнер зарегистрирован.";
                        } else {
                            message = "Ошибка регистрации термоконтейнера. Повторите.";
                        }
                    }
                }
            } else {
                message = "Данный объект указан как конечный получатель. \nНеобходимо оформить приемку термоконтейнера на вкладке ПРИЕМКА ТЕРМОКОНТЕЙНЕРА.";
            }
        } else {
            Container container = containerService.findByContainerNumber(containerNumber);
            if(container!=null){
                message = "Не найдено отправление с данным номером термоконтейнера. \nНеобходимо зарегистрировать отправку термоконтейнера из данного объекта на вкладке ОТГРУЗКА ТЕРМОКОНТЕЙНЕРА.";
            } else {
                message = "Отсутствующий в базе номер термоконтейнера. \nПроверьте правильность ввода номера.";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }


    @PostMapping("/user/check-in/check-route-off")
    public  void checkEndPoint (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        String text = req.getParameter("text");
        User user = getUserFromAuthentication();
        String dateString = req.getParameter("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime currentDateTime = LocalDateTime.parse(dateString, formatter);
        Department toDepartment = departmentService.findDepartmentById(user.getDepartmentId());
        Container container = containerService.findByContainerNumber(containerNumber);
        if(container.getDepartment().getId()!=toDepartment.getId()) {
            ContainerNote note = containerNoteService.findNoteByContainer(containerNumber);
            if(note.getId()>0) {
                note.setToUser(user);
                note.setArriveTime(currentDateTime);
                LocalDateTime sendDateTime = note.getSendTime();
                LocalDateTime waitDateTime = sendDateTime.plusHours(note.getTimeStandard());
                note.setDelayTime(getDelay(currentDateTime, waitDateTime));
                note.setArriveNote(text);
                note.setSend(false);
                container.setDepartment(toDepartment);
                note.setContainer(container);
                if(containerNoteService.saveNote(note)){
                    message = "Изменение маршрута следования термоконтейнера зарегистрировано.";
                } else {
                    message = "Ошибка записи изменения маршрута следования термоконтейнера. Повторите.";
                }
            } else {
                message = "Не найден термоконтейнер. \nВ базе не зарегистрирована отправка термоконтейнера из объекта последней приемки";
            }
        } else {
            message = "Прибытие данного термоконтейнера уже зарегистрировано.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-in/check")
    public  void checkInContainer (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        User user = getUserFromAuthentication();
        String dateString = req.getParameter("date");
        String text = req.getParameter("text");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime currentDateTime = LocalDateTime.parse(dateString, formatter);
        Department toDepartment = departmentService.findDepartmentById(user.getDepartmentId());
        Container container = containerService.findByContainerNumber(containerNumber);
        String regId = container.getDepartment().getId() + "";
        String depId = toDepartment.getId() + "";
        if(regId.equals(depId)) {
            message = "Прибытие данного термоконтейнера уже зарегистрировано.";
        } else {
            ContainerNote note = containerNoteService.findNoteByContainerAndToDepartment(containerNumber, user.getDepartmentId());
            if (note.getId() != null) {
                note.setToUser(user);
                note.setArriveTime(currentDateTime);
                LocalDateTime sendDateTime = note.getSendTime();
                LocalDateTime waitDateTime = sendDateTime.plusHours(note.getTimeStandard());
                Long delayHours = getDelay(currentDateTime, waitDateTime);
                note.setDelayTime(getDelay(currentDateTime, waitDateTime));
                note.setArriveNote(text);
                note.setSend(false);
                container.setDepartment(toDepartment);
                note.setContainer(container);
                if(delayHours>3){
                    if(emailService.sendDelayNote(delayHours, note)){
                        note.setDelayNote("Оповещение о нарушении срока доставки разослано");
                    } else {
                        note.setDelayNote("Ошибка почтового сервиса. Оповещение о нарушении срока доставки не было разослано");
                    }
                }
                if(containerNoteService.saveNote(note)){
                    message = "Прибытие термоконтейнера внесено в базу.";
                } else {
                    message = "Ошибка регистрации прибытия термоконтейнера. Повторите.";
                }
            } else {
                message = "Данный объект не является конечным получателем термоконтейнера. \n" +
                        "Промежуточную регистрацию по маршруту следования термоконтейнера следует оформить на вкладке ПРОМЕЖУТОЧНЫЙ ОБЪЕКТ РЕГИСТРАЦИИ.\n\n" +
                        "ЖЕЛАЕТЕ ПРЕРВАТЬ МАРШРУТ СЛЕДОВАНИЯ ТЕРМОКОНТЕЙНЕРА И ОФОРМИТЬ ЕГО ПРИЕМКУ?" +
                        "\nНе забудьте указать причину прерывания маршрута следования термоконтейнера! ";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private Long getDelay(LocalDateTime currentDateTime, LocalDateTime waitDateTime) {
        long delay = 0;
        long days;
        long hours;
        if(currentDateTime.isAfter(waitDateTime)){
            days = waitDateTime.until(currentDateTime, ChronoUnit.DAYS);
            hours = waitDateTime.until(currentDateTime, ChronoUnit.HOURS);
            delay = 24*days + hours;
        }
        return delay;
    }


}
