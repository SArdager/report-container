package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.*;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.repositories.ContainerNoteRepository;
import kz.kdlolymp.termocontainers.repositories.ContainerValueRepository;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
public class AccountController {
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
    private String message;
    private Gson gson = new Gson();


    @PostMapping("/user/check-container/new-container")
    public  void viewCheckNewContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        int valueId = Integer.parseInt(req.getParameter("valueId"));
        String dateString = req.getParameter("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        ContainerValue value = containerValueService.findContainerValueById(valueId);
        User user = getUserFromAuthentication();
        Container container = new Container();
        container.setContainerNumber(containerNumber);
        container.setValue(value);
        container.setRegistrationDate(dateTime);
        Department department = departmentService.findDepartmentById(user.getDepartmentId());
        container.setDepartment(department);
        if(containerService.addNewContainer(container)) {
            message = "Новый термоконтейнер внесен в базу";
        } else {
            message = "В базе зарегистрирован термоконтейнер с данным номером";
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
    public  void SendContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        Container container = containerService.findByContainerNumber(containerNumber);
        User user = getUserFromAuthentication();
        if(container!=null && container.getDepartment().getId()==user.getDepartmentId()) {
            if(containerNoteService.isContainerSend(container)){
                message = "Данный термоконтейнер уже оформлен на отправку. \nИзменить место назначения доставки?";
            } else {
                int departmentId = Integer.parseInt(req.getParameter("departmentId"));
                int timeStandard = Integer.parseInt(req.getParameter("timeStandard"));
                String dateString = req.getParameter("date");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
                ContainerNote containerNote = new ContainerNote();
                containerNote.setContainer(container);
                Department outDepartment = departmentService.findDepartmentById(user.getDepartmentId());
                containerNote.setOutDepartment(outDepartment);
                Department toDepartment = departmentService.findDepartmentById(departmentId);
                containerNote.setToDepartment(toDepartment);
                containerNote.setOutUser(user);
                containerNote.setSendTime(dateTime);
                containerNote.setTimeStandard(timeStandard);
                containerNote.setSend(true);
                if (containerNoteService.saveNote(containerNote)) {
                    ContainerNote savedNote = containerNoteService.findSentContainer(container);
                    message = "Оформлена отгрузка термоконтейнера получателю.\n Номер документа отгрузки: " +
                            savedNote.getId();
                } else {
                    message = "Ошибка оформления отгрузки. Повторите попытку";
                }
            }
        } else {
            message = "Термоконтейнер не зарегистрирован в базе данных или находится на другом объекте. \nНеобходимо вначале его зарегистрировать или оформить передачу на данный объект.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
    @PostMapping("/user/check-container/resend-container")
    public  void NewSendContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
        req.setCharacterEncoding("UTF-8");
        String containerNumber = req.getParameter("containerNumber");
        Container container = containerService.findByContainerNumber(containerNumber);
        ContainerNote containerNote = containerNoteService.findSentContainer(container);
        if(containerNote!=null) {
            int departmentId = Integer.parseInt(req.getParameter("departmentId"));
            int timeStandard = Integer.parseInt(req.getParameter("timeStandard"));
            String dateString = req.getParameter("date");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
            Department toDepartment = departmentService.findDepartmentById(departmentId);
            containerNote.setToDepartment(toDepartment);
            containerNote.setSendTime(dateTime);
            containerNote.setTimeStandard(timeStandard);
            ContainerNote savedNote = noteRepository.save(containerNote);
            if (savedNote != null) {
                message = "Оформлена отгрузка термоконтейнера новому получателю.\n Номер документа отгрузки: " +
                        savedNote.getId();
            } else {
                message = "Ошибка оформления отгрузки. Повторите попытку";
            }
        } else {
            message = "Термоконтейнер не зарегистрирован в базе данных или находится на другом объекте. \nНеобходимо вначале его зарегистрировать.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-container/search-container")
    public  void searchInDepartment(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("UTF-8");
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        List<Container> containers = containerService.findAllByBranchId(branchId);
        gson = new GsonBuilder()
                .registerTypeAdapter(Container.class, new ContainerSerializer())
                .registerTypeAdapter(Department.class, new DepartmentSerializer())
                .create();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(gson.toJson(containers));
        resp.getWriter().flush();
        resp.getWriter().close();
    }
    @PostMapping("/user/check-container/find-container")
    public  void findByNumber(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("UTF-8");
        String findNumber = req.getParameter("findNumber");
        ContainerNote containerNote = containerNoteService.findLastNoteByContainer(findNumber);
        gson = new GsonBuilder()
                .registerTypeAdapter(ContainerNote.class, new ContainerNoteSerializer())
                .registerTypeAdapter(BetweenPoint.class, new BetweenPointSerializer())
                .create();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(gson.toJson(containerNote));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/check-container/print-code")
    public  void printCodeContainer(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("UTF-8");
        int startNumber = Integer.parseInt(req.getParameter("startNumber"));
        int endNumber = Integer.parseInt(req.getParameter("endNumber"));
        boolean isCompleted = false;
        String barcode;
        for(int number = startNumber; number<endNumber+1; number++){
            barcode = number + "";
            int length = barcode.length();
            if(length<12){
                for(int i=length; i<12; i++){
                    barcode = "0" + barcode;
                }
            }
            BufferedImage image = generateEAN13BarcodeImage(barcode);
            File file = new File("E:/Pictures/barcodes/" + barcode + ".jpg");
            ImageIO.write(image, "jpg", file);
            isCompleted = true;
        }

        if(isCompleted) {
            message = "Рисунки штрих-кодов сохранены";
        } else {
            message = "Ошибка оформления штрих-кодов. Повтрите попытку";
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
}
