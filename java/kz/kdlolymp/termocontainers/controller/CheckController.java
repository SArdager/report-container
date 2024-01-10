package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.*;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.entity.Container;
import kz.kdlolymp.termocontainers.repositories.ContainerNoteRepository;
import kz.kdlolymp.termocontainers.service.*;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class CheckController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ContainerNoteRepository noteRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private ContainerNoteService containerNoteService;
    @Autowired
    private ContainerValueService containerValueService;
    @Autowired
    private DefaultEmailService emailService;

    private String message;
    private Gson gson = new Gson();


    @PostMapping("/user/check-container/new-container")
    public void checkInNewContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        String nextContainerNumber = req.getParameter("nextContainerNumber");
        int valueId = Integer.parseInt(req.getParameter("valueId"));
        String dateString = LocalDateTime.now().toString().substring(0,16);
        LocalDateTime dateTime = LocalDateTime.parse(dateString);
        message = "";
        if(nextContainerNumber.length()==8){
            String baseNumber = containerNumber.substring(0, 5);
            int first = Integer.parseInt(containerNumber.substring(5));
            int second = Integer.parseInt(nextContainerNumber.substring(5));
            if(first<second){
                for(int i=first; i<second+1; i++){
                    message += checkNewContainer(baseNumber, i, valueId, dateTime);
                }
            } else {
                for(int i=second; i<first+1; i++){
                    message += checkNewContainer(baseNumber, i, valueId, dateTime);
                }
            }
        } else {
            message += checkNewContainer(containerNumber, -1, valueId, dateTime);
            if(message.contains("имеется")){
                message = "В базе зарегистрирован термоконтейнер с данным номером. <br>Подтвердите или отмените изменение вида термоконтейнера ";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String checkNewContainer(String baseNumber, int number, int valueId, LocalDateTime dateTime) {
        String containerNumber = baseNumber;
        if(number>-1){
            if(number<10){
                containerNumber += "00" + number;
            } else if(number<100 && number>9){
                containerNumber += "0" + number;
            } else {
                containerNumber += number;
            }
        }
        ContainerValue value = containerValueService.findContainerValueById(valueId);
        User user = getUserFromAuthentication();
        String answer = "";
        if(user!=null) {
            Container container = new Container();
            container.setContainerNumber(containerNumber);
            container.setValue(value);
            container.setRegistrationDate(dateTime);
            container.setEnable(true);
            Department department = departmentService.findDepartmentById(user.getDepartmentId());
            container.setDepartment(department);
            if (containerService.addNewContainer(container)) {
                answer = "Термоконтейнер с номером: " + containerNumber + " внесен в базу<br>";
            } else {
                answer = "В базе имеется термоконтейнер с номером: " + containerNumber + ". Изменение вида термоконтейнера нельзя произвести для диапазона номеров<br>";
            }
        }
        return answer;
    }

    @PostMapping("/user/check-container/edit-container")
    public void editContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        int valueId = Integer.parseInt(req.getParameter("valueId"));
        ContainerValue value = containerValueService.findContainerValueById(valueId);
        Container container = containerService.findByContainerNumber(containerNumber);
        container.setValue(value);
        if (containerService.saveContainer(container)) {
            message = "Внесены изменения в характеристики термоконтейнера.";
        } else {
            message = "Ошибка изменения базы данных. Перегрузите страницу.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-container/check-place")
    public void checkPlaceOffContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        User user = getUserFromAuthentication();
        if(user!=null) {
            int departmentId = user.getDepartmentId();
            Container container = containerService.findByContainerNumber(containerNumber);
            if (container != null && container.getContainerNumber() != null) {
                if (containerNoteService.checkNotesByContainerId(container.getId())) {
                    if (departmentId == container.getDepartment().getId()) {
                        message = "Списать";
                    } else {
                        ContainerNote note = containerNoteService.findLastNoteByContainer(containerNumber);
                        if (note != null && note.getToDepartment() != null) {
                            if (departmentId == note.getToDepartment().getId()) {
                                message = "Принять";
                            } else {
                                message = "Отправка термоконтейнера с указанным номером в отдел логистики для списания не была зарегистрирована. НЕобходимо оформить отгрузку.";
                            }
                        }
                    }
                } else {
                    message = "Удалить";
                }
            } else {
                message = "Термоконтейнер с указанным номером в системе не зарегистрирован";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-container/check-in")
    public void checkInContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        User user = getUserFromAuthentication();
        if(user!=null) {
            int departmentId = user.getDepartmentId();
            Container container = containerService.findByContainerNumber(containerNumber);
            String dateString = LocalDateTime.now().toString().substring(0, 16);
            LocalDateTime currentDateTime = LocalDateTime.parse(dateString);
            if (container != null) {
                ContainerNote note = containerNoteService.findLastNoteByContainer(containerNumber);
                if (note != null && note.isSend()) {
                    if (departmentId == note.getToDepartment().getId()) {
                        Department toDepartment = departmentService.findDepartmentById(departmentId);
                        note.setToUser(user);
                        note.setArriveTime(currentDateTime);
                        note.setSend(false);
                        container.setDepartment(toDepartment);
                        if(containerService.saveContainer(container)) {
                            note.setContainer(container);
                            containerNoteService.saveNote(note);
                        }
                        message = "Списать";
                    } else {
                        message = "Термоконтейнер с указанным номером для отправки в отдел логистики для списания не был зарегистрирован";
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

    @PostMapping("/user/check-container/write-off-container")
    public void writeOffContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");

        String dateString = LocalDateTime.now().toString().substring(0,16);
        LocalDateTime currentDate = LocalDateTime.parse(dateString);

        Container container = containerService.findByContainerNumber(containerNumber);
        if (containerNoteService.checkNotesByContainerId(container.getId())) {
            container.setReleaseDate(currentDate);
            container.setEnable(false);
            if (containerService.saveContainer(container)) {
                message = "Термоконтейнер списан.";
            } else {
                message = "Ошибка списания термоконтейнера. Перегрузите страницу";
            }
        } else {
            containerService.deleteContainer(container.getId());
            message = "Термоконтейнер удален";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/edit-container/edit-values")
    public void changeContainerValue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String valueName = req.getParameter("valueName");
        ContainerValue newValue = new ContainerValue();
        newValue.setValueName(valueName);
        if (id > 1) {
            newValue.setId(id);
            containerValueService.saveValue(newValue);
            message = "Название вида термоконтейнера изменено";
        } else {
            if (containerValueService.addNewValue(newValue)) {
                message = "Новый вид термоконтейнера создан";
            } else {
                message = "Такой вид термоконтейнера зарегистрирован в базе данных";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/edit-container/delete-value")
    public void deleteContainerValue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        ContainerValue value = containerValueService.findContainerValueById(id);
        if (containerService.checkContainerForValue(value)) {
            message = "Данный вид термоконтейнера удалять нельзя, так как в базе данных имеются термоконтейнеры, использующие данный вид";
        } else {
            containerValueService.deleteContainerValue(id);
            message = "Вид термоконтейнера удален";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
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

    @PostMapping("/user/check-container/send-container")
    public void CheckSendContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        String nextContainerNumber = req.getParameter("nextContainerNumber");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        int timeStandard = Integer.parseInt(req.getParameter("timeStandard"));
        message = "";
        if(nextContainerNumber.length()==8){
            String baseNumber = containerNumber.substring(0, 5);
            int first = Integer.parseInt(containerNumber.substring(5));
            int second = Integer.parseInt(nextContainerNumber.substring(5));
            if(first<second){
                for(int i=first; i<second+1; i++){
                    message += sendContainer(baseNumber, i, departmentId, timeStandard);
                }
            } else {
                for(int i=second; i<first+1; i++){
                    message += sendContainer(baseNumber, i, departmentId, timeStandard);
                }
            }
        } else {
            message += sendContainer(containerNumber, -1, departmentId, timeStandard);
            if(message.contains("зарегистрирована")){
                message = "Термоконтейнер с номером: " + containerNumber + " уже оформлен на отправку. <br>Изменить место назначения доставки?";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String sendContainer(String baseNumber, int number, int departmentId, int timeStandard) {
        String containerNumber = baseNumber;
        if(number>-1){
            if(number<10){
                containerNumber += "00" + number;
            } else if(number<100 && number>9){
                containerNumber += "0" + number;
            } else {
                containerNumber += number;
            }
        }
        Container container = containerService.findByContainerNumber(containerNumber);
        User user = getUserFromAuthentication();
        String answer = "";
        if(user!=null) {
            if (container != null && container.isEnable() && container.getDepartment().getId() == user.getDepartmentId()) {
                if (containerNoteService.isContainerSend(container)) {
                    answer = "Отправка термоконтейнера с номером: " + containerNumber + " зарегистрирована. Изменение места назначения нельзя произвести для диапазона номеров.<br>";
                } else {
                    Department outDepartment = departmentService.findDepartmentById(user.getDepartmentId());
                    Department toDepartment = departmentService.findDepartmentById(departmentId);
                    String dateString = LocalDateTime.now().toString().substring(0, 16);
                    LocalDateTime dateTime = LocalDateTime.parse(dateString);
                    ContainerNote containerNote = noteRepository.save(new ContainerNote(container, outDepartment));
                    containerNote.setToDepartment(toDepartment);
                    containerNote.setOutUser(user);
                    containerNote.setSendTime(dateTime);
                    containerNote.setTimeStandard(timeStandard);
                    containerNote.setSend(true);
                    if (containerNoteService.saveNote(containerNote)) {
                        answer = "Оформлена отгрузка термоконтейнера с номером: " + containerNumber + ".<br>";
                    } else {
                        answer = "Ошибка оформления термоконтейнера с номером: " + containerNumber + ".<br>";
                    }
                }
            } else {
                answer = "Термоконтейнер с номером: " + containerNumber + " не зарегистрирован (списан) в базе данных или находится на другом объекте. <br>Необходимо вначале его зарегистрировать или оформить передачу на данный объект.<br>";
            }
        }
        return answer;
    }

    @PostMapping("/user/check-container/resend-container")
    public void NewSendContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        Container container = containerService.findByContainerNumber(containerNumber);
        ContainerNote containerNote = containerNoteService.findSentContainer(container);
        if (containerNote != null) {
            int departmentId = Integer.parseInt(req.getParameter("departmentId"));
            int timeStandard = Integer.parseInt(req.getParameter("timeStandard"));
            String dateString = LocalDateTime.now().toString().substring(0,16);
            LocalDateTime dateTime = LocalDateTime.parse(dateString);
            Department toDepartment = departmentService.findDepartmentById(departmentId);
            containerNote.setToDepartment(toDepartment);
            containerNote.setSendTime(dateTime);
            containerNote.setTimeStandard(timeStandard);
            ContainerNote savedNote = noteRepository.save(containerNote);
            if (savedNote != null) {
                message = "Оформлена отгрузка термоконтейнера новому получателю.<br>Номер документа отгрузки: " +
                        savedNote.getId();
            } else {
                message = "Ошибка оформления отгрузки. Перегрузите страницу попытку";
            }
        } else {
            message = "Термоконтейнер не зарегистрирован в базе данных или находится на другом объекте. <br>Необходимо вначале его зарегистрировать.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-container/search-container")
    public void searchInDepartment(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("UTF-8");
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        List<Container> containers;
        if (branchId > 1) {
            containers = containerService.findAllByBranchId(branchId);
        } else {
            containers = containerService.findAllOrdered();
        }
        gson = new GsonBuilder()
                .registerTypeAdapter(Container.class, new ContainerSerializer())
                .registerTypeAdapter(Department.class, new DepartmentContainerSerializer())
                .create();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(gson.toJson(containers));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-container/no-used-container")
    public void searchNoUsedContainers(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("UTF-8");
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        Long days = Long.parseLong(req.getParameter("days"));
        String dateString = LocalDateTime.now().toString().substring(0,16);
        LocalDateTime currentDate = LocalDateTime.parse(dateString);
        LocalDateTime controlDate = currentDate.minusDays(days);
        List<Container> containers;
        if (branchId > 1) {
            containers = containerService.findAllNoUsedByBranchId(branchId, controlDate, currentDate);
        } else {
            containers = containerService.findAllNoUsedOrdered(controlDate, currentDate);
        }
        gson = new GsonBuilder()
                .registerTypeAdapter(Container.class, new ContainerSerializer())
                .registerTypeAdapter(Department.class, new DepartmentContainerSerializer())
                .create();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(gson.toJson(containers));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-container/find-container")
    public void findByNumber(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("UTF-8");
        String findNumber = req.getParameter("findNumber");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String endDateString = req.getParameter("endDate");
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);;
        if(endDateString!=null && endDateString.length()>0) {
            LocalDate endDate = LocalDate.parse(endDateString, formatter);
            LocalTime endTime = LocalTime.of(23, 59);
            endDateTime = LocalDateTime.of(endDate,endTime);
        }
        List<ContainerNote> notes = containerNoteService.findNotesByContainer(findNumber, endDateTime);
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

    @PostMapping("/user/check-container/find-container-place")
    public void findContainerByNumber(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("UTF-8");
        String findNumber = req.getParameter("findNumber");
        ContainerNote note = containerNoteService.findLastNoteByContainer(findNumber);
        if(note==null || note.getContainer()==null){
            Container container = containerService.findByContainerNumber(findNumber);
            if(container!=null) {
                note = new ContainerNote(container, container.getDepartment());
            }
        }
        gson = new GsonBuilder()
                .registerTypeAdapter(ContainerNote.class, new ContainerNoteSerializer())
                .registerTypeAdapter(BetweenPoint.class, new BetweenPointSerializer())
                .create();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(gson.toJson(note));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-container/print-code")
    public void printCodeContainer(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = getUserFromAuthentication();
        if(user!=null) {
            String parcelBarcode = req.getParameter("startNumber");
            String endNumberString = req.getParameter("endNumber");
            if (endNumberString != null && endNumberString.length() > 0) {
                long startNumber = Long.parseLong(parcelBarcode);
                long endNumber = Long.parseLong(endNumberString);
                int count = (int) (endNumber - startNumber + 1);
                int ind = 0;
                String barcode;
                File[] attachments = new File[count];
                for (long number = startNumber; number < endNumber + 1; number++) {
                    barcode = String.valueOf(number);
                    int length = barcode.length();
                    if (length < 8) {
                        for (int i = length; i < 8; i++) {
                            barcode = 0 + barcode;
                        }
                    }
//            BufferedImage image = generateEAN13BarcodeImage(barcode);
                    BufferedImage image = generateCode128BarcodeImage(barcode);
                    File file = new File(barcode + ".jpg");
                    ImageIO.write(image, "jpg", file);
                    attachments[ind] = file;
                    ind++;
                }
                Thread sendMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        emailService.sendEmailWithMultiAttachment(user.getEmail(), "Рисунки штрих-кодов",
                                "Во вложении сформированные рисунки штрих-кодов, начиная с номера: " + startNumber +
                                        "; по номер (включительно): " + endNumber, attachments);
                    }
                });
                sendMessage.start();
                message = "Рисунки штрих-кодов отправлены на почту";
            } else {
                File file = new File(parcelBarcode + ".jpg");

                Barcode barcode = BarcodeFactory.createCode128(parcelBarcode);
                barcode.setDrawingText(true);
                barcode.setBarHeight(50);
                barcode.setBarWidth(2);
                BarcodeImageHandler.saveJPEG(barcode, file);
                Thread sendMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            emailService.sendEmailWithFileAttachment(user.getEmail(), "Рисунок штрих-кода",
                                    "Во вложении сформированный рисунок штрих-кода почтового отправления с номером: " + parcelBarcode, file);
                        } catch (MessagingException me) {
                        } catch (FileNotFoundException fe) {
                        }
                    }
                });
                sendMessage.start();
                message = "Рисунок штрих-кода отправлен на почту";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    public static BufferedImage generateEAN13BarcodeImage(String barcodeText) throws Exception {
        Barcode barcode = BarcodeFactory.createEAN13(barcodeText);

        return BarcodeImageHandler.getImage(barcode);
    }
    public static BufferedImage generateCode128BarcodeImage(String barcodeText) throws Exception {
        Barcode barcode = BarcodeFactory.createCode128(barcodeText);

        return BarcodeImageHandler.getImage(barcode);
    }


}
