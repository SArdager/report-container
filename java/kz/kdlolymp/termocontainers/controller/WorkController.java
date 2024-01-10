package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.RouteParcelSerializer;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WorkController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private CompanyService companyService;
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
    private BetweenPointService betweenPointService;
    @Autowired
    private AlarmGroupService alarmGroupService;
    @Autowired
    private DefaultEmailService emailService;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private ParcelPointService parcelPointService;
    private String message;
    private Gson gson = new Gson();

    @RequestMapping("/work-starter")
    public String viewWorkStarter(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            Department department = departmentService.findDepartmentById(departmentId);
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("userRights", userRights);
            if(userRights.getRights().equals("ДОСТАВКА")){
                return "redirect: user/check-courier";
            } else {
                return "work-starter";
            }
        } else {
            return "redirect: login";
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

    private UserRights chooseNameRights(int departmentId, List<UserRights> userRightsList) {
        UserRights userRights = new UserRights();
        String rights = "";
        for (int i = 0; i < userRightsList.size(); i++) {
            userRights = userRightsList.get(i);
            if (userRights.getDepartment().getId() == departmentId) {
                rights = userRights.getRights();
            }
        }
        if (rights.equals("editor")) {
            rights = "ВНЕСЕНИЕ ЗАПИСЕЙ";
        } else if (rights.equals("courier")) {
            rights = "ДОСТАВКА";
        } else if (rights.equals("reader")) {
            rights = "ПРОСМОТР ЗАПИСЕЙ";
        } else if (rights.equals("changer")) {
            rights = "ПРОСМОТР ЖУРНАЛОВ И ИЗМЕНЕНИЕ СРОКОВ ДОСТАВКИ";
        } else if (rights.equals("righter")) {
            rights = "ПРОСМОТР ЖУРНАЛОВ И ИЗМЕНЕНИЕ ПРАВ";
        } else if (rights.equals("chef")) {
            rights = "ПОЛНЫЕ ПРАВА ПО ЛАБОРАТОРИИ";
        } else if (rights.equals("creator")) {
            rights = "СОЗДАНИЕ И ОТСЛЕЖИВАНИЕ ПОСЫЛОК";
        } else {
            rights = "УЧЕТ ТЕРМОКОНТЕЙНЕРОВ";
        }
        userRights.setRights(rights);
        return userRights;
    }

    @RequestMapping("/user/check-in")
    public String viewCheckIn(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            Department department = departmentService.findDepartmentById(departmentId);
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("userRights", userRights);
            return "user/check-in";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/user/check-between")
    public String viewCheckBetween(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            Department department = departmentService.findDepartmentById(departmentId);
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("userRights", userRights);
            return "user/check-between";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/user/check-courier")
    public String viewCheckCourier(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            if(userRights.getRights() == "ДОСТАВКА") {
                Department department = departmentService.findDepartmentById(departmentId);
                model.addAttribute("user", user);
                model.addAttribute("department", department);
                model.addAttribute("userRights", userRights);
                return "user/check-courier";
            } else {
                return "redirect: ../work-starter";
            }
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/user/check-out")
    public String viewCheckOut(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            String preferences = user.getMemory();
            Department department = departmentService.findDepartmentById(departmentId);
            List<Company> companies= companyService.findAll();
            List<Branch> branchesDB= branchService.findAllLaborSorted();
            List<Branch> branches = new ArrayList<>();
            if(preferences!=null && preferences.indexOf("branch")>0){
                int end = preferences.indexOf("}", preferences.indexOf("branch")+5);
                preferences = preferences.substring(preferences.indexOf("branch")+8, end);
                String branchesString = preferences;
                while(branchesString.indexOf("[")>0){
                    branchesString = branchesString.substring(0, branchesString.indexOf("[")) +
                            branchesString.substring(branchesString.indexOf("]")+1);

                }
                branchesString.trim();
                String[] branchesSet = branchesString.split(" ");
                for(int i=0; i<branchesSet.length; i++){
                    int id = Integer.parseInt(branchesSet[i]);
                    for(Branch branch: branchesDB){
                        if(branch.getId() == id){
                            branches.add(branch);
                            break;
                        }
                    }
                }
            } else {
                Branch userBranch = department.getBranch();
                branches.add(userBranch);
                for(Branch branch: branchesDB){
                    if(branch.getId() != userBranch.getId()){
                        branches.add(branch);
                    }
                }
            }
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("companies", companies);
            model.addAttribute("branches", branches);
            model.addAttribute("userRights", userRights);
            return "user/check-out";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/user/setup")
    public String viewSetup(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            Department department = departmentService.findDepartmentById(departmentId);
            List<Branch> branches = branchService.findAllLaborSorted();
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("branches", branches);
            return "user/setup";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/user/check-journal")
    public String viewCheckJournal(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<Branch> branches = branchService.findAllLaborSorted();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            Department department = departmentService.findDepartmentById(departmentId);
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("branches", branches);
            model.addAttribute("userRights", userRights);
            return "user/check-journal";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/user/check-parcel")
    public String viewCheckParcel(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<Company> companies = companyService.findAll();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            Department department = departmentService.findDepartmentById(departmentId);
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("companies", companies);
            model.addAttribute("userRights", userRights);
            return "user/check-parcel";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/user/check-container")
    public String viewCheckContainer(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            Department department = departmentService.findDepartmentById(departmentId);
            List<ContainerValue> containerValues = containerValueService.findAll();
            List<Branch> branches = branchService.findAllLaborSorted();
            model.addAttribute("containerValues", containerValues);
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("branches", branches);
            model.addAttribute("userRights", userRights);
            return "user/check-container";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/user/create-parcel")
    public String viewCreateParcel(Model model) {
        User user = getUserFromAuthentication();
        if (user!=null) {
            int departmentId = user.getDepartmentId();
            List<UserRights> userRightsList = user.getUserRightsList();
            UserRights userRights = chooseNameRights(departmentId, userRightsList);
            Department department = departmentService.findDepartmentById(departmentId);
            List<Company> companies = companyService.findAll();
            List<Branch> branches = branchService.findAllLaborSorted();
            int memoryDepartmentId = 0;
            int memoryBranchId = 0;
            if (user.getMemory() != null && user.getMemory().length() > 0) {
                String memory = user.getMemory();
                if(memory.indexOf("dep")>-1){
                    memoryDepartmentId = Integer.parseInt(memory.substring(memory.indexOf("dep")+5, memory.indexOf("]")));
                    Department memoryDepartment = departmentService.findDepartmentById(memoryDepartmentId);
                    memoryBranchId = memoryDepartment.getBranch().getId();
                }
            }
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("companies", companies);
            model.addAttribute("branches", branches);
            model.addAttribute("userRights", userRights);
            model.addAttribute("memoryDepartmentId", memoryDepartmentId);
            model.addAttribute("memoryBranchId", memoryBranchId);
            return "user/create-parcel";
        } else {
            return "redirect: ../login";
        }
    }


    @PostMapping("/user/check-out/send-parcel")
    public void checkOutParcel(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String parcelNumber = req.getParameter("parcelNumber");
        Parcel parcel = parcelService.findParcelByNumber(parcelNumber);
        if(parcel!=null) {
            if (parcel.getParentNumber() != null && parcel.getParentNumber().length() > 0) {
                message = "Нельзя оформлять отгрузку вложенной посылки. <br>Для отгрузки необходимо оформить основную (транспортную) посылку или термоконтейнер.";
            } else {
                Long payment = Long.parseLong(req.getParameter("payment"));
                User user = getUserFromAuthentication();
                if(user!=null) {
                    if (parcel.isWaited() && parcel.getCurrentDepartmentId() == user.getDepartmentId()) {
                        message = "Оформлена отгрузка почтового отправления: ";
                        List<Parcel> childParcels = parcelService.getWaitedParcelsByParent(parcelNumber);

                        Long parentPayment = payment;

                        if (childParcels != null && childParcels.size() > 0) {
                            for (Parcel childParcel : childParcels) {
                                Long childPayment = 0L;
                                if (payment > 0) {
                                    childPayment = childParcel.getCostsPart() * payment / 100;
                                    parentPayment = parentPayment - childPayment;
                                }
                                message += sendParcel(req, childParcel, childPayment, parcelNumber);
                            }
                        }
                        message += sendParcel(req, parcel, parentPayment, "");
                    } else {
                        message = "Посылка не зарегистрирована на этом объекте или уже оформлена ее отгрузка. <br>Необходимо вначале оформить ее передачу на данный объект.";
                    }
                }
            }
        } else{
            message = "Посылка не найдена.<br>Проверьте правильность ввода номера.<br>Перед оформлением отгрузки посылку необходимо вначале зарегистрировать.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String sendParcel(HttpServletRequest req, Parcel parcel, Long payment, String parentNumber) {
        String result = "";
        if (parcel != null) {
            int departmentId = Integer.parseInt(req.getParameter("toId"));
            Department department = departmentService.findDepartmentById(departmentId);
            String parcelNumber = parcel.getParcelNumber();
            String text = "";
            if (req.getParameter("text") != null && req.getParameter("text").length() > 0) {
                text = "Отправитель: " + req.getParameter("text");
            }
            String dateString = LocalDateTime.now().toString().substring(0, 16);
            LocalDateTime sendDate = LocalDateTime.parse(dateString);
            User sendUser = getUserFromAuthentication();
            if (sendUser != null) {
                int fromDepartmentId = sendUser.getDepartmentId();
                ParcelPoint point = parcelPointService.findParcelPoint(parcelNumber, fromDepartmentId);
                point.setOutUser(sendUser);
                point.setSendTime(sendDate);
                point.setToDepartment(department);
                point.setPayment(payment);
                point.setParent(parentNumber);
                point.setText(text);
                parcelPointService.savePoint(point);
                ParcelPoint newPoint = new ParcelPoint(parcel.getId(), parcelNumber, department, parentNumber, 0L);
                parcelPointService.savePoint(newPoint);
                parcel.addParcelPoint(newPoint);
                parcel.setWaited(false);
                parcel.setCurrentDepartmentId(departmentId);
                parcel.setParentNumber(parentNumber);
                parcelService.saveParcel(parcel);
                result = parcelNumber + "; ";
                if (parcel.isInformation()) {
                    sendDepartureMessage(parcel, fromDepartmentId, department, sendDate);
                }
            }
        }
        return result;
    }

    @PostMapping("/user/check-out/send")
    public void checkOutContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        Container container = containerService.findByContainerNumber(containerNumber);
        if(container!=null) {
            User user = getUserFromAuthentication();
            if(user!=null) {
                if (container.getDepartment().getId() == user.getDepartmentId()) {
                    if (containerNoteService.isContainerSend(container)) {
                        message = "Данный термоконтейнер уже оформлен на отправку. <br>Изменить место назначения доставки?";
                    } else {
                        message = sendContainer(req, container, 0L);
                    }
                } else {
                    message = "Термоконтейнер зарегистрирован на другом объекте. <br>Необходимо вначале оформить его передачу на данный объект.";
                }
            }
        } else {
            message = "Термоконтейнер не найден. Проверьте правильность ввода штрих-кода.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String sendContainer(HttpServletRequest req, Container container, Long noteId) {
        int toDepartmentId = Integer.parseInt(req.getParameter("toId"));
        Long payment = Long.parseLong(req.getParameter("payment"));
        int amount = Integer.parseInt(req.getParameter("amount"));
        boolean paidEnd = Boolean.parseBoolean(req.getParameter("paidEnd"));
        String text = req.getParameter("text");
        String thermometer = req.getParameter("thermometer");
        String dateString = LocalDateTime.now().toString().substring(0,16);
        LocalDateTime dateTime = LocalDateTime.parse(dateString);
        User user = getUserFromAuthentication();
        if(user!=null) {
            int outDepartmentId = user.getDepartmentId();
            Department outDepartment = departmentService.findDepartmentById(outDepartmentId);
            Department toDepartment = departmentService.findDepartmentById(toDepartmentId);
            int timeStandard = 0;
            TimeStandard standard = timeStandardService.findByParameters(outDepartmentId, toDepartmentId);
            if (standard != null && standard.getTimeStandard() > 0) {
                timeStandard = standard.getTimeStandard();
            } else {
                Thread sendMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendTimeAlarmMessage(outDepartment, toDepartment);
                    }
                });
                sendMessage.start();
            }
            ContainerNote containerNote;
            if (noteId > 0) {
                containerNote = containerNoteService.findContainerNoteById(noteId);
            } else {
                containerNote = new ContainerNote();
                containerNote.setContainer(container);
                containerNote.setOutDepartment(outDepartment);
            }
            containerNote.setToDepartment(toDepartment);
            containerNote.setOutUser(user);
            containerNote.setSendNote(text);
            containerNote.setSendTime(dateTime);
            containerNote.setThermometer(thermometer);
            containerNote.setSendPay(payment);
            containerNote.setAmount(amount);
            containerNote.setPaidEnd(paidEnd);
            containerNote.setTimeStandard(timeStandard);
            containerNote.setSend(true);
            if (timeStandard == 0) {
                containerNote.setDelayNote("Отправка оповещения об отсутствии в системе срока доставки");
            }
            if (containerNoteService.saveNote(containerNote)) {
                message = "Оформлена отгрузка термоконтейнера получателю: <br>" + toDepartment.getDepartmentName() + ", " +
                        toDepartment.getBranch().getBranchName() + ".<br>Номер документа отгрузки: " + containerNote.getId();
            }
            List<Parcel> childParcels = parcelService.getWaitedParcelsByParent(container.getContainerNumber());
            if (childParcels != null && childParcels.size() > 0) {
                message += "<br>Вложено почтовое отправление: ";
                for (Parcel childParcel : childParcels) {
                    message += sendParcel(req, childParcel, 0L, container.getContainerNumber());
                    List<Parcel> grandsonParcels = parcelService.getWaitedParcelsByParent(childParcel.getParcelNumber());
                    if (grandsonParcels != null && grandsonParcels.size() > 0) {
                        for (Parcel grandsonParcel : grandsonParcels) {
                            message += sendParcel(req, grandsonParcel, 0L, childParcel.getParcelNumber());
                        }
                    }
                }
            }
        } else {
            message = "Перегрузите страницу.";
        }
        return message;
    }

    private void sendTimeAlarmMessage(Department outDepartment, Department toDepartment) {
        List<User> users = alarmGroupService.getAlarmUsersByDepartmentId(outDepartment.getId());
        if (users != null && users.size() > 0) {
            emailService.sendTimeStandardNote(users, toDepartment.getId(), outDepartment.getId());
        } else {
            emailService.sendMessageToAdmin("Отсутствует лицо для информирования об отсутствии времени доставки между объектами: \n" +
                    outDepartment.getDepartmentName() + ", " + outDepartment.getBranch().getBranchName() + " и " +
                    "\nи " + toDepartment.getDepartmentName() + ", " + toDepartment.getBranch().getBranchName() + ".");
        }
    }

    private void sendDelayAlarmMessage(int outDepartmentId, int toDepartmentId, ContainerNote note, Long delayHours) {
        List<User> alarmUsers = new ArrayList<>();
        if (toDepartmentId != outDepartmentId) {
            List<User> toDepartmentUsers = alarmGroupService.getAlarmUsersByDepartmentId(toDepartmentId);
            List<User> outDepartmentUsers = alarmGroupService.getAlarmUsersByDepartmentId(outDepartmentId);
            if (toDepartmentUsers != null && toDepartmentUsers.size() > 0) {
                for (User toUser : toDepartmentUsers) {
                    alarmUsers.add(toUser);
                }
            }
            if (outDepartmentUsers != null && outDepartmentUsers.size() > 0) {
                for (User outUser : toDepartmentUsers) {
                    alarmUsers.add(outUser);
                }
            }
        } else {
            alarmUsers = alarmGroupService.getAlarmUsersByDepartmentId(toDepartmentId);
        }
        if (alarmUsers != null && alarmUsers.size() > 0) {
            emailService.sendDelayNote(alarmUsers, delayHours, note);
        }
    }

    @PostMapping("/user/check-out/again-send")
    public void checkOutAgainContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        Container container = containerService.findByContainerNumber(containerNumber);
        Long noteId = containerNoteService.findSentContainer(container).getId();
        message = sendContainer(req, container, noteId);
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-out/save-changes")
    public void changesOutContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        Long noteId = Long.parseLong(req.getParameter("noteId"));
        ContainerNote note = containerNoteService.findContainerNoteById(noteId);
        boolean isOutChange = Boolean.parseBoolean(req.getParameter("isOutChange"));
        Long payment = 0L;
        if (isOutChange) {
            int departmentToId = Integer.parseInt(req.getParameter("toDepartment"));
            if (!note.isPaidEnd() && req.getParameter("changePay").length() > 0) {
                payment = Long.parseLong(req.getParameter("changePay"));
                note.setSendPay(payment);
            }
            String text = req.getParameter("changeNote");
            Department toDepartment = departmentService.findDepartmentById(departmentToId);
            note.setToDepartment(toDepartment);
            if (text.length() > 0) {
                note.setSendNote(text);
            }
        } else {
            int departmentId = Integer.parseInt(req.getParameter("departmentId"));
            String arriveText = req.getParameter("changeArriveNote");
            String betweenText = req.getParameter("changeBetweenNote");
            if (note.isPaidEnd() && req.getParameter("changePay").length() > 0) {
                payment = Long.parseLong(req.getParameter("changePay"));
                note.setSendPay(payment);
            }
            if (arriveText.length() > 0) {
                note.setArriveNote(arriveText);
            } else {
                List<BetweenPoint> points = note.getBetweenPoints();
                for (BetweenPoint point : points) {
                    if (point.getDepartment().getId() == departmentId) {
                        point.setPointNote(betweenText);
                    }
                }
            }
        }
        if (containerNoteService.saveNote(note)) {
            message = "В маршрутный лист термоконтейнера внесены изменения.<br>Для просмотра изменений перезагрузите данные по перемещению термоконтейнера";
        } else {
            message = "Ошибка записи изменений. Перегрузите страницу";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-between/check")
    public void checkBetweenContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        String text = req.getParameter("text");
        User user = getUserFromAuthentication();
        if(user!=null) {
            Department betweenDepartment = departmentService.findDepartmentById(user.getDepartmentId());
            ContainerNote note = containerNoteService.findNoteByContainer(containerNumber);
            if (note != null && note.getId() != null) {
                Department outDepartment = note.getOutDepartment();
                if (outDepartment.getBranch().getId() != betweenDepartment.getBranch().getId()) {
                    message = "Термоконтейнер отправлен с объекта ДРУГОГО филиала! <br>" +
                            "Вы уверены, что ввели правильный номер термоконтейнера и регистрируете его прохождение по маршруту транспортировки?";
                } else {
                    message = checkBetweenPoint(note, user, betweenDepartment, text);
                }
            } else {
                Container container = containerService.findByContainerNumber(containerNumber);
                if (container != null) {
                    message = "Не найдено отправление с данным номером термоконтейнера. <br>Необходимо зарегистрировать отправку термоконтейнера (на вкладке ОТГРУЗКА ТЕРМОКОНТЕЙНЕРА).";
                } else {
                    message = "Отсутствующий в базе номер термоконтейнера. <br>Проверьте правильность ввода номера.";
                }
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-between/check-again")
    public void checkAgainBetweenContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        String text = req.getParameter("text");
        User user = getUserFromAuthentication();
        if(user!=null) {
            Department betweenDepartment = departmentService.findDepartmentById(user.getDepartmentId());
            ContainerNote note = containerNoteService.findNoteByContainer(containerNumber);
            if (note != null && note.getId() != null) {
                message = checkBetweenPoint(note, user, betweenDepartment, text);
            } else {
                message = "Данный номер термоконтейнера не оформлен. <br>В месте отгрузки необходимо зарегистрировать отправку этого термоконтейнера.";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String checkBetweenPoint(ContainerNote note, User user, Department betweenDepartment, String text){
        String answer = "";
        String dateString = LocalDateTime.now().toString().substring(0,16);
        LocalDateTime currentDateTime = LocalDateTime.parse(dateString);
        Department toDepartment = note.getToDepartment();
        if (toDepartment.getId()!=betweenDepartment.getId()) {
            BetweenPoint point = new BetweenPoint();
            point.setContainerNote(note);
            point.setPassUser(user);
            point.setDepartment(betweenDepartment);
            point.setPassTime(currentDateTime);
            point.setPointNote(text);
            List<BetweenPoint> points = new ArrayList<>();
            boolean isChecked = false;
            if (note.getBetweenPoints() != null) {
                points = note.getBetweenPoints();
                for (int i = 0; i < points.size(); i++) {
                    if (points.get(i).getDepartment().getId() == betweenDepartment.getId()) {
                        isChecked = true;
                    }
                }
            }
            if (isChecked) {
                answer = "Попытка повторной регистрации. <br>Данный термоконтейнер уже зарегистрирован.";
            } else {
                if (betweenPointService.addNewPoint(point)) {
                    points.add(point);
                    note.setBetweenPoints(points);
                    if (containerNoteService.saveNote(note)) {
                        answer = "Термоконтейнер зарегистрирован.";
                    } else {
                        answer = "Ошибка регистрации термоконтейнера. Перегрузите страницу.";
                    }
                }
            }
        } else {
            answer = "Данный объект указан как конечный получатель. <br>Необходимо оформить приемку термоконтейнера на вкладке ПРИЕМКА ТЕРМОКОНТЕЙНЕРА.";
        }
        return answer;
    }

    @PostMapping("/user/check-between/check-courier")
    public void checkCourier(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        String text = req.getParameter("text");
        User user = getUserFromAuthentication();
        if(user!=null) {
            Department betweenDepartment = departmentService.findDepartmentById(user.getDepartmentId());
            ContainerNote note = containerNoteService.findNoteByContainer(containerNumber);
            if (note != null && note.getId() != null) {
                String dateString = LocalDateTime.now().toString().substring(0, 16);
                LocalDateTime currentDateTime = LocalDateTime.parse(dateString);
                BetweenPoint point = new BetweenPoint();
                point.setContainerNote(note);
                point.setPassUser(user);
                point.setDepartment(betweenDepartment);
                point.setPassTime(currentDateTime);
                point.setPointNote(text);
                List<BetweenPoint> points = new ArrayList<>();
                boolean isChecked = false;
                if (note.getBetweenPoints() != null) {
                    points = note.getBetweenPoints();
                    for (int i = 0; i < points.size(); i++) {
                        if (points.get(i).getDepartment().getId() == betweenDepartment.getId()) {
                            LocalDateTime regDateTime = points.get(i).getPassTime();
                            if (regDateTime.isAfter(currentDateTime.minusMinutes(15))) {
                                isChecked = true;
                            }
                        }
                    }
                }
                if (isChecked) {
                    message = "Попытка повторной регистрации в течение 15 минут. <br>Данный термоконтейнер уже зарегистрирован.";
                } else {
                    if (betweenPointService.addNewPoint(point)) {
                        points.add(point);
                        note.setBetweenPoints(points);
                        if (containerNoteService.saveNote(note)) {
                            message = "Термоконтейнер зарегистрирован.";
                        } else {
                            message = "Ошибка регистрации термоконтейнера. Перегрузите страницу.";
                        }
                    }
                }
            } else {
                message = "Данный номер термоконтейнера не оформлен. <br>В месте отгрузки необходимо зарегистрировать отправку этого термоконтейнера.";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }


    @PostMapping("/user/check-in/check-route-off")
    public void checkEndPoint(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        String text = req.getParameter("text");
        User user = getUserFromAuthentication();
        if(user!=null) {
            String dateString = LocalDateTime.now().toString().substring(0, 16);
            LocalDateTime currentDateTime = LocalDateTime.parse(dateString);
            Department toDepartment = departmentService.findDepartmentById(user.getDepartmentId());
            Container container = containerService.findByContainerNumber(containerNumber);
            ContainerNote note = containerNoteService.findNoteByContainer(containerNumber);
            if (note != null && note.getSendTime() != null) {
                note.setToUser(user);
                note.setArriveTime(currentDateTime);
                LocalDateTime sendDateTime = note.getSendTime();
                LocalDateTime waitDateTime = sendDateTime.plusHours(note.getTimeStandard());
                note.setDelayTime(getDelay(currentDateTime, waitDateTime));
                note.setArriveNote(text);
                note.setToDepartment(toDepartment);
                note.setSend(false);
                container.setDepartment(toDepartment);
                if(containerService.saveContainer(container)) {
                    note.setContainer(container);
                    if (containerNoteService.saveNote(note)) {
                        message = "Изменение маршрута следования термоконтейнера зарегистрировано.";
                    } else {
                        message = "Ошибка записи изменения маршрута следования термоконтейнера. Перегрузите страницу.";
                    }
                }
            } else {
                message = "Термоконтейнер не найден. Проверьте отгрузку термоконтейнера по журналу движения.";
            }
            message += getParcelsForCheck(text, containerNumber);
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-in/check")
    public void checkInContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        User user = getUserFromAuthentication();
        if(user!=null) {
            int toDepartmentId = user.getDepartmentId();
            String text = "";
            if (req.getParameter("text") != null) {
                text = req.getParameter("text");
            }
            String dateString = LocalDateTime.now().toString().substring(0, 16);
            LocalDateTime currentDateTime = LocalDateTime.parse(dateString);
            Department toDepartment = departmentService.findDepartmentById(toDepartmentId);
            Container container = containerService.findByContainerNumber(containerNumber);
            if (container != null) {
                ContainerNote note = containerNoteService.findLastNoteByContainer(containerNumber);
                if (note != null && note.getOutDepartment() != null) {
                    int outDepartmentId = note.getOutDepartment().getId();
                    if (note.isSend()) {
                        if (toDepartmentId == note.getToDepartment().getId()) {
                            note.setToUser(user);
                            note.setArriveTime(currentDateTime);
                            LocalDateTime sendDateTime = note.getSendTime();
                            LocalDateTime waitDateTime = sendDateTime.plusHours(note.getTimeStandard());
                            Long delayHours = getDelay(currentDateTime, waitDateTime);
                            note.setDelayTime(delayHours);
                            note.setArriveNote(text);
                            note.setSend(false);
                            container.setDepartment(toDepartment);
                            if(containerService.saveContainer(container)) {
                                note.setContainer(container);
                                if (delayHours > 8) {
                                    Thread sendMessage = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendDelayAlarmMessage(outDepartmentId, toDepartmentId, note, delayHours);
                                        }
                                    });
                                    sendMessage.start();
                                    note.setDelayNote("Рассылка оповещения о нарушении срока доставки более 8 часов");
                                }
                            }
                            if (containerNoteService.saveNote(note)) {
                                if (delayHours > 0) {
                                    message = "Прибытие термоконтейнера внесено в базу. Опоздание доставки - " + delayHours + " часов.";
                                } else {
                                    message = "Прибытие термоконтейнера внесено в базу. Доставлено вовремя<br>";
                                }
                            } else {
                                message = "Ошибка регистрации прибытия термоконтейнера. Перегрузите страницу.";
                            }
                            message += getParcelsForCheck(text, containerNumber);
                        } else {
                            message = "Данный объект не является конечным получателем термоконтейнера. <br>" +
                                    "Промежуточную регистрацию по маршруту следования термоконтейнера следует оформлять на вкладке ПРОМЕЖУТОЧНЫЙ ОБЪЕКТ РЕГИСТРАЦИИ (по ссылке - 'Регистрация на объекте').<br><br>" +
                                    "При необходимости приемки термоконтейнера на Вашем объекте, свяжитесь с регистратором места, откуда был отправлен термоконтейнер, чтобы он поменял объект доставки на ваш ПЗБ (лабораторию).";
                        }
                    } else {
                        if (toDepartmentId == note.getToDepartment().getId()) {
                            message = "Прибытие данного термоконтейнера уже зарегистрировано.";
                        } else {
                            message = "Данный термоконтейнер зарегистрирован на другом объекте и его отправка не была зарегистрирована.";
                        }
                    }
                } else {
                    message = "Термоконтейнер не найден. Проверьте правильность ввода штрих-кода и необходимость регистрации прибытия.";
                }
            } else {
                message = "Термоконтейнер не найден. Проверьте правильность ввода штрих-кода.";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String getParcelsForCheck(String text, String containerNumber) {
        List<Parcel> parcels = parcelService.getMovedParcelsByParent(containerNumber);
        String result = "";
        if (parcels != null && parcels.size() > 0) {
            result = "<br>Вложено почтовое отправление: ";
            for (Parcel parcel : parcels) {
                parcel.setParentNumber("");
                parcelService.saveParcel(parcel);
                result += checkInParcel(text, parcel);
            }
        }
        return result;
    }

    @PostMapping("/user/check-in/parcel")
    public void parcelCheckIn(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String parcelNumber = req.getParameter("parcelNumber");
        Parcel parcel = parcelService.findParcelByNumber(parcelNumber);
        String text = "";
        if(req.getParameter("text")!=null){
            text = req.getParameter("text");
        }
        User user = getUserFromAuthentication();
        if(user!=null) {
            int toDepartmentId = user.getDepartmentId();
            if (parcel != null) {
                ParcelPoint point = parcelPointService.findParcelPoint(parcelNumber, toDepartmentId);
                if (parcel.getParentNumber() != null && parcel.getParentNumber().length() > 0) {
                    message = "Нельзя зарегистрировать вложенную посылку.<br>Необходимо регистрировать прибытие термоконтейнера или основной (транспортной) посылки.";
                } else {
                    if (parcel.isDelivered()) {
                        message = "Нельзя принять данную посылку, так как она уже доставлена в пункт назначения.<br>Необходимо оформить новую посылку.";
                    } else if (parcel.getSendDate() == null) {
                        message = "ВНИМАНИЕ! Почтовое отправление с данным номером сформировано, но не оформлена отгрузка!<br>Нельзя регистрировать прибытие!";
                    } else if (point == null) {
                        message = "Данный объект не является пунктом получения почтового отправления. <br>" +
                                "Если необходимо принять посылку на Вашем объекте, то свяжитесь с регистратором места, откуда посылка была оформлена, чтобы он поменял объект доставки на Ваш.";
                    } else {
                        message = "Зарегистрировано прибытие почтового отправления: ";
                        message += checkInParcel(text, parcel);
                    }
                }
            } else {
                message = "Посылка не найдена. Проверьте правильность ввода номера.";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-in/parcel-change-point")
    public void parcelChangeCheckIn(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String parcelNumber = req.getParameter("parcelNumber");
        Parcel parcel = parcelService.findParcelByNumber(parcelNumber);
        String text = "";
        if(req.getParameter("text")!=null){
            text = req.getParameter("text");
        }
        if(parcel!=null) {
            message = "Зарегистрировано прибытие почтового отправления: ";
            message += checkInParcel(text, parcel);
        } else {
            message = "Посылка не найдена. Проверьте правильность ввода номера.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String checkInParcel(String text, Parcel parcel) {
        String result = "";
        result += registerParcel(text, parcel);
        List<Parcel> childParcels = parcelService.getMovedParcelsByParent(parcel.getParcelNumber());
        if (childParcels != null && childParcels.size() > 0) {
            for (Parcel childParcel : childParcels) {
                result += registerParcel(text, childParcel);
            }
        }
        return result;
    }

    private String registerParcel(String text, Parcel parcel) {
        User user = getUserFromAuthentication();
        String result = "";
        String dateString = LocalDateTime.now().toString().substring(0,16);
        LocalDateTime currentDateTime = LocalDateTime.parse(dateString);
        if(parcel!=null && user!=null) {
            String parcelNumber = parcel.getParcelNumber();
            int departmentId = user.getDepartmentId();
            Department department = departmentService.findDepartmentById(departmentId);
            ParcelPoint point = parcelPointService.findParcelPoint(parcelNumber, departmentId);
            if (point != null) {
                point.setIntoUser(user);
                point.setArriveTime(currentDateTime);
                if (text.length() > 0) {
                    if (point.getText() != null) {
                        point.setText(point.getText() + " <br>Получатель: " + text);
                    } else {
                        point.setText("Получатель: " + text);
                    }
                }
                parcelPointService.savePoint(point);
                result = parcelNumber + "";
                result += checkDestination(parcel, departmentId, currentDateTime);
            } else {
                ParcelPoint outPoint = parcelPointService.findOutParcelPoint(parcelNumber);
                outPoint.setDepartment(department);
                outPoint.setIntoUser(user);
                outPoint.setArriveTime(currentDateTime);
                if (text.length() > 0) {
                    if (outPoint.getText() != null) {
                        outPoint.setText(point.getText() + " <br>Получатель: Изменен пункт доставки. " + text);
                    } else {
                        outPoint.setText("Получатель: Изменен пункт доставки. " + text);
                    }
                }
                parcelPointService.savePoint(outPoint);
                result = "<br>ВНИМАНИЕ! Регистрация посылки " + parcelNumber + " не совпадает с пунктом доставки!!! ";
                result += checkDestination(parcel, departmentId, currentDateTime);
            }
        }
        return result;
    }

    private String checkDestination(Parcel parcel, int departmentId, LocalDateTime currentDate) {
        String result = "; ";
        int destinationId = parcel.getDestination().getId();
        if(destinationId==departmentId){
            parcel.setDelivered(true);
            parcel.setDeliveryDate(currentDate);
            List<ParcelPoint> points = parcel.getParcelPoints();
            Long payment = 0L;
            for(ParcelPoint point: points){
                payment += point.getPayment();
            }
            parcel.setPayment(payment);
            result = " (прибытие в пункт назначения); ";
        } else {
            parcel.setWaited(true);
            parcel.setCurrentDepartmentId(departmentId);
        }
        parcelService.saveParcel(parcel);
        if(parcel.isInformation()){sendArrivalMessage(parcel, departmentId, currentDate); }
        return result;
    }
    private void sendDepartureMessage(Parcel parcel, int fromDepartmentId, Department department, LocalDateTime sendDate){
        Department fromDepartment = departmentService.findDepartmentById(fromDepartmentId);
        String postMessage = "Почтовое отправление №" + parcel.getParcelNumber() + ", \nотправленное " +
                parcel.getSendDate() + " \nполучателю: " + parcel.getDestination().getBranch().getBranchName() +
                ", " + parcel.getDestination().getDepartmentName() + ":  \n" + sendDate + " зарегистрирована отгрузка \nиз: " +
                fromDepartment.getBranch().getBranchName() + ", " + fromDepartment.getDepartmentName() + " \nв: " +
                department.getBranch().getBranchName() + ", " + department.getDepartmentName();
        String toAddress = parcel.getSendUser().getEmail();
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    emailService.sendSimpleEmail(toAddress, "Регистрация отгрузки почтового отправления", postMessage);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        sendMessage.start();
    }

    private void sendArrivalMessage(Parcel parcel, int departmentId, LocalDateTime currentDate){
        Department department = departmentService.findDepartmentById(departmentId);
        String postMessage = "Почтовое отправление №" + parcel.getParcelNumber() + ", \nотправленное " +
                parcel.getSendDate() + " \nполучателю: " + parcel.getDestination().getBranch().getBranchName() +
                ", " + parcel.getDestination().getDepartmentName() + ", \nприбыло " + currentDate + " \nв " +
                department.getBranch().getBranchName() + ", " + department.getDepartmentName();
        String toAddress = parcel.getSendUser().getEmail();
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    emailService.sendSimpleEmail(toAddress, "Регистрация прибытия почтового отправления", postMessage);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        sendMessage.start();
    }

    private Long getDelay(LocalDateTime currentDateTime, LocalDateTime waitDateTime) {
        long delay = 0;
        long days;
        long hours;
        if (currentDateTime.isAfter(waitDateTime)) {
            days = waitDateTime.until(currentDateTime, ChronoUnit.DAYS);
            hours = waitDateTime.until(currentDateTime, ChronoUnit.HOURS);
            delay = 24 * days + hours;
        }
        return delay;
    }

    @PostMapping("/user/check-parcel/create")
    public void createParcel(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        int typeNumber = Integer.parseInt(req.getParameter("parcelType"));
        int outDepartmentId = Integer.parseInt(req.getParameter("outDepartmentId"));
        int destinationId = Integer.parseInt(req.getParameter("destinationId"));
        String dimensions = req.getParameter("dimensions");
        String note = req.getParameter("note");
        boolean information = Boolean.parseBoolean(req.getParameter("information"));
        String dateString = LocalDateTime.now().toString().substring(0,16);
        LocalDateTime createDate = LocalDateTime.parse(dateString);
        User createUser = getUserFromAuthentication();
        String parcelType;
        switch (typeNumber) {
            case (1):
                parcelType = "K";
                break;
            case (2):
                parcelType = "P";
                break;
            case (3):
                parcelType = "M";
                break;
            case (4):
                parcelType = "O";
                break;
            case (5):
                parcelType = "C";
                break;
            default:
                parcelType = "";
                break;
        }
        String parcelNumber = parcelService.addNewParcel(parcelType, destinationId, outDepartmentId,
                createUser, dimensions, note, information, createDate, createUser, true);

        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(parcelNumber);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-parcel/load-results")
    public void loadParcels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        List<Parcel> parcels = parcelService.getWaitedParcelsByDepartmentId(departmentId);
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Parcel.class, new RouteParcelSerializer())
                .create();
        resp.getWriter().print(gson.toJson(parcels));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-parcel/memory-department")
    public void memoryDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        User user = getUserFromAuthentication();
        if(user!=null) {
            String memory = user.getMemory();
            if (departmentId > 1) {
                if (memory != null && memory.length() > 0) {
                    if (memory.indexOf("dep") > -1) {
                        memory = "dep [" + departmentId + memory.substring(memory.indexOf("]"));
                    } else {
                        memory = "dep [" + departmentId + "]" + memory;
                    }
                } else {
                    memory = "dep [" + departmentId + "]";
                }
                user.setMemory(memory);
                userService.saveUser(user);
                message = "Сохранен объект отправки";
            } else {
                message = "Необходимо выбрать объект отправки";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-parcel/send")
    public void sendFirstParcel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("toDepartmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        String parcelNumber = req.getParameter("parcelNumber");
        String dateString = LocalDateTime.now().toString().substring(0,16);
        LocalDateTime sendDate = LocalDateTime.parse(dateString);
        User sendUser = getUserFromAuthentication();
        if(sendUser!=null) {
            Parcel parcel = parcelService.findParcelByNumber(parcelNumber);
            if (parcel.getParentNumber() != null && parcel.getParentNumber().length() > 0) {
                message = "Нельзя оформлять отгрузку вложенной посылки. <br>Для отгрузки необходимо оформить основную (транспортную) посылку.";
            } else {
                message = "Оформлена отгрузка почтового отправления: ";
                message += sendNewParcel(parcel, department, sendDate, sendUser, "");
                List<Parcel> childParcels = parcelService.findAllByParentNumber(parcelNumber);
                if (childParcels != null && childParcels.size() > 0) {
                    for (Parcel childParcel : childParcels) {
                        if (childParcel != null) {
                            message += sendNewParcel(childParcel, department, sendDate, sendUser, parcelNumber);
                        }
                    }
                }
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String sendNewParcel(Parcel parcel, Department department, LocalDateTime sendDate, User sendUser, String parent) {
        String result = "";
        ParcelPoint point = new ParcelPoint(parcel.getId(), parcel.getParcelNumber(), department, parent, 0L);
        parcelPointService.savePoint(point);
        parcel.setSendDate(sendDate);
        parcel.setSendUser(sendUser);
        parcel.setWaited(false);
        parcel.setCurrentDepartmentId(department.getId());
        parcel.setParentNumber(parent);
        parcel.addParcelPoint(point);
        if (parcelService.saveParcel(parcel)) {
            result = parcel.getParcelNumber() + "; ";
        } else {
            result = "Ошибка записи по " + parcel.getParcelNumber() + "; ";
        }
        return result;
    }

    @PostMapping("/user/check-parcel/add-to-parent")
    public void addParcel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String parcelNumber = req.getParameter("parcelNumber");
        User user = getUserFromAuthentication();
        Parcel parcel = parcelService.findParcelByNumber(parcelNumber);
        if (user!=null && parcel!=null && parcel.getParcelNumber() != null) {
            if (parcel.isWaited()) {
                if (parcel.getParentNumber() != null && parcel.getParentNumber().length() > 0) {
                    message = "Нельзя оформить вложение этой посылки, так она уже вложена в другую посылку. <br>Вначале необходимо ее выложить.";
                } else {
                    String parentNumber = req.getParameter("parentNumber");
                    int departmentId = user.getDepartmentId();
                    if (Character.isDigit(parentNumber.charAt(0))) {
                        Container container = containerService.findByContainerNumber(parentNumber);
                        if (container != null && container.getDepartment().getId() == departmentId) {
                            message = setParentNumber(parcel, parentNumber, 0);
                        } else {
                            message = "Указанный номер термоконтейнера, в который вкладывается посылка, не найден на данном объекте." +
                                    "<br>Проверьте правильность ввода номера термоконтейнера.";
                        }
                    } else {
                        int costsPart = Integer.parseInt(req.getParameter("costsPart"));
                        if(costsPart<0) {costsPart = 0;}
                        int totalCosts = 0;
                        Parcel parentParcel = parcelService.getWaitedParcelsByNumberAndDepartment(parentNumber, user.getDepartmentId());
                        if (parentParcel != null) {
                            List<Parcel> childParcels = parcelService.getWaitedParcelsByParent(parcelNumber);
                            if(childParcels!= null && childParcels.size()>0) {
                                message = "Нельзя оформить вложение этой посылки, так как в нее вложены другие посылки. " +
                                        "<br>Вначале необходимо выложить из нее посылки и вложить их все по отдельности в основную.";
                            } else {
                                List<Parcel> parcels = parcelService.findAllByParentNumber(parentNumber);
                                for (Parcel existParcel : parcels) {
                                    totalCosts += existParcel.getCostsPart();
                                }
                                if (totalCosts + costsPart > 80) {
                                    message = "Сумма долей всех вложенных посылок в оплате доставки не должна превышать 80%. " +
                                            "<br>Доля доставки уже вложенных посылок составляет: " + totalCosts + "%";
                                } else {
                                    message = setParentNumber(parcel, parentNumber, costsPart);
                                }
                            }
                        } else {
                            message = "Указанный номер основной посылки, в который вкладывается выбранная посылка, не найден на объекте." +
                                    "<br>Проверьте правильность ввода номера посылки.";
                        }
                    }
                }
            } else {
                message = "Почтовое отправление не найдено на данном объекте или оформлена его отгрузка";
            }
        } else {
            message = "Проверьте правильность ввода номера почтового отправления";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String setParentNumber(Parcel parcel, String parentNumber, int costsPart) {
        parcel.setParentNumber(parentNumber);
        parcel.setCostsPart(costsPart);
        parcelService.saveParcel(parcel);
        String result = "Почтовое отправление вложено в основную транспортную посылку (термоконтейнер)";
        return result;
    }

    @PostMapping("/user/check-parcel/remove-from-parent")
    public void removeFromParent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String parcelNumber = req.getParameter("parcelNumber");
        Parcel parcel = parcelService.findParcelByNumber(parcelNumber);
        if (parcel!=null && parcel.getParentNumber() != null && parcel.getParentNumber().length() > 0) {
            parcel.setParentNumber("");
            if (parcelService.saveParcel(parcel)) {
                message = "Посылка выгружена из транспортной посылки и готова к отгрузке";
            } else {
                message = "Ошибка обращения в базу данных. Перегрузите страницу.";
            }
        } else {
            message = "Посылка не была вложена в другую посылку или такой посылки нет";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

}

