package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.SentContainerNoteSerializer;
import kz.kdlolymp.termocontainers.controller.serializers.UserSerializer;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRightsService userRightsService;
    @Autowired
    private AlarmGroupService alarmGroupService;
    @Autowired
    private DefaultEmailService emailService;

    private Gson gson = new Gson();
    private String message ="";

    @RequestMapping("/admin")
    public String viewAdminStarter(Model model){
        return "/admin";
    }
    @RequestMapping("/admin/reset-password")
    public String viewResetPassword(){
        return "/admin/reset-password";
    }

    @RequestMapping("/admin/edit-user")
    public String viewEditUser(){
        return "/admin/edit-user";
    }

    @RequestMapping("/admin/info-users")
    public String findUserPage(){
        return "/admin/info-users";
    }

    @GetMapping("/admin/add-user")
    public String registration(Model model){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Object principal = authentication.getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userService.findByUsername(username);
        List<Company> companies = companyService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("userForm", new User());
        model.addAttribute("companies", companies);
        return "/admin/add-user";
    }

    @GetMapping("/admin/edit-rights")
    public String rightsEditor(Model model){
        List<Company> companies = companyService.findAll();
        model.addAttribute("companies", companies);
        return "/admin/edit-rights";
    }
    @RequestMapping("/admin/alarm-groups")
    public String editAlarmGroups(Model model){
        List<AlarmGroup> alarmGroups = alarmGroupService.findAll();
        model.addAttribute("alarmGroups", alarmGroups);
        return "/admin/alarm-groups";
    }

    @PostMapping("/admin/add-user/save-user")
    public  void addUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");
        User userForm = new User();
        List<UserRights> userRightsList = new ArrayList<>();
        Long curatorId = Long.parseLong(req.getParameter("curatorId"));
        userForm.setUsername(req.getParameter("username"));
        userForm.setUserSurname(req.getParameter("userSurname"));
        userForm.setUserFirstname(req.getParameter("userFirstname"));
        userForm.setPosition(req.getParameter("position"));
        userForm.setEmail(req.getParameter("email"));
        userForm.setRole(req.getParameter("role"));
        userForm.setDepartmentId(Integer.parseInt(req.getParameter("departmentId")));
        if(curatorId!=null){
            userForm.setCurator(userService.findUserById(curatorId));
        } else {
            userForm.setCurator(null);
        }
        userForm.setPassword(req.getParameter("password"));
        userForm.setEnabled(true);
        userForm.setTemporary(true);
        userForm.setUserRightsList(userRightsList);
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        UserRights userRights = new UserRights();
        if(userService.addNewUser(userForm)){
            User user = userService.findByUsername(userForm.getUsername());
            userRights.setDepartment(department);
            userRights.setRights(req.getParameter("rights"));
            user.addUserRights(userRights);
            userRightsService.addNewUserGroup(userRights);
            userService.saveUser(user);
            message = "Пользователь " + user.getUserSurname() + " " + user.getUserFirstname() + " добавлен.";
        } else  {
            message = "Пользователь с логином " + userForm.getUsername() + " уже имеется.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/search-user")
    public void searchUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String text = req.getParameter("text").trim() + "%";
        List<User> usersList = userService.getUsersByPartUsername(text);
        List<User> users = new ArrayList<>();
        if(usersList.size()>0){
            for(User userFromList: usersList){
                User user = new User();
                user.setId(userFromList.getId());
                user.setUsername(userFromList.getUsername());
                user.setUserFirstname(userFromList.getUserFirstname());
                user.setUserSurname(userFromList.getUserSurname());
                user.setDepartmentId(userFromList.getDepartmentId());
                user.setRole(userFromList.getRole());
                users.add(user);
            }
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(users));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-rights/rights")
    public  void changeRights(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Long id = Long.parseLong(req.getParameter("id"));
        String role = req.getParameter("role");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        String rights = req.getParameter("rights");
        User user = userService.findUserById(id);
        Department department = departmentService.findDepartmentById(departmentId);
        if(role.equals("ADMIN")){
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        List<UserRights> userRightsList = user.getUserRightsList();
        boolean isRightsExist = false;
        for(int i = 0; i< userRightsList.size(); i++){
            UserRights userRights = userRightsList.get(i);
            int currentDepartmentId = userRights.getDepartment().getId();
            if(currentDepartmentId == departmentId){
                isRightsExist = true;
                userRights.setRights(rights);
            }
        }
        if(!isRightsExist){
            UserRights newUserRights = new UserRights();
            newUserRights.setDepartment(department);
            newUserRights.setRights(rights);
            newUserRights.setUser(user);
            UserRights userRights = userRightsService.addNewUserGroup(newUserRights);
            user.addUserRights(userRights);

        }
        if(userService.saveUser(user)){
            message = "Пользователю " + user.getUserSurname() + " " + user.getUserFirstname() + " добавлены права.";
// TODO send email to username
        } else {
            message = "Ошибка изменения прав пользователя";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-alarm-group/change-group")
    public  void changeAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(req.getParameter("id"));
        String alarmGroupName = req.getParameter("alarmGroupName");
        if(id > 0){
            AlarmGroup alarmGroup = alarmGroupService.findAlarmGroupById(id);
            alarmGroup.setAlarmGroupName(alarmGroupName);
            if(alarmGroupService.saveAlarmGroup(alarmGroup)){
                message = "Название группы оповещения изменено.";
            } else {
                message = "Ошибка изменения при записи в базу данных. Повторите.";
            }
        } else {
            AlarmGroup alarmGroup = alarmGroupService.findAlarmGroupByName(alarmGroupName);
            if(alarmGroup!=null){
                message = "Группа оповещения с таким названием уже имеется. Измените название.";
            } else {
                AlarmGroup newAlarmGroup = new AlarmGroup();
                newAlarmGroup.setAlarmGroupName(alarmGroupName);
                List<User> users = new ArrayList<>();
                newAlarmGroup.setUsers(users);
                if (alarmGroupService.addNewAlarmGroup(newAlarmGroup)) {
                    message = "Создана новая группа оповещения.";
                } else {
                    message = "Ошибка при создании новой группы оповещения. Повторите.";
                }
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
    @PostMapping("/admin/edit-alarm-group/delete-group")
    public  void deleteAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(req.getParameter("id"));

        if(alarmGroupService.deleteAlarmGroup(id)){
            message = "Группа оповещения удалена.";
        } else {
            message = "Ошибка удаления группы оповещения. Повторите.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-alarm-group/add-user")
    public  void addUserToAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int alarmGroupId = Integer.parseInt(req.getParameter("alarmGroupId"));
        Long userId = Long.parseLong(req.getParameter("userId"));
        User user = userService.findUserById(userId);
        if(alarmGroupService.addNewUserToAlarmGroup(user, alarmGroupId)){
            message = "Пользователь добавлен в группу оповещения.";
        } else {
            message = "Пользователь уже имеется в группе оповещения.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-alarm-group/remove-user")
    public  void removeUserFromAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int alarmGroupId = Integer.parseInt(req.getParameter("alarmGroupId"));
        Long userId = Long.parseLong(req.getParameter("userId"));
        User user = userService.findUserById(userId);
        if(alarmGroupService.removeUserFromAlarmGroup(user, alarmGroupId)){
            message = "Пользователь удален из группы оповещения.";
        } else {
            message = "Ошибка удаления пользователя из группы оповещения. Повторите.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
    @PostMapping("/admin/edit-alarm-group/get-user-group")
    public  void getUsersFromAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int alarmGroupId = Integer.parseInt(req.getParameter("alarmGroupId"));
        List<User> users = alarmGroupService.getUsersById(alarmGroupId);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(User.class, new UserSerializer());
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(builder.create().toJson(users));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/reset-password")
    public  void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Long id = Long.parseLong(req.getParameter("id"));
        String password = req.getParameter("password");
        User user = userService.findUserById(id);
        user.setPassword(password);
        user.setTemporary(true);
        String toAddress = user.getEmail();
        if(userService.changePassword(user)){
            if (emailService.sendTemporaryPassword(toAddress, password)) {
                message = "Временный пароль выслан на адрес корпоративной электронной почты пользователю:  " + user.getUserSurname() + " " + user.getUserFirstname();
            } else {
                message = "Ошибка отправки разового пароля на адрес электронной почты пользователя: " + user.getUserSurname() + " " + user.getUserFirstname() +
                        "\nПовторите позднее или направьте разовый пароль:  " + password + "  пользователю со своего почтового сервиса на адрес: " + toAddress;
            }
        } else {
            message = "Ошибка сброса пароля пользователя";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-user")
    public  void editUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Long id = Long.parseLong(req.getParameter("id"));
        String userSurname = req.getParameter("userSurname");
        String userFirstname = req.getParameter("userFirstname");
        String position = req.getParameter("position");
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        Long curatorId = Long.parseLong(req.getParameter("curatorId"));
        boolean isEnabled = Boolean.parseBoolean(req.getParameter("isEnabled"));
        User user = userService.findUserById(id);
        user.setUserSurname(userSurname);
        user.setUserFirstname(userFirstname);
        user.setPosition(position);
        user.setEmail(email);
        user.setUsername(username);
        user.setEnabled(isEnabled);
        if(curatorId>0) {
            User curator = userService.findUserById(curatorId);
            user.setCurator(curator);
        } else {
            user.setCurator(null);
        }

        if(userService.saveUser(user)){
            message = "Данные пользователя *" + user.getUserSurname() + " " + user.getUserFirstname() + "* изменены.";
        } else {
            message = "Ошибка редактирования пользователя";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/find-user")
    public  void findUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String surname = "";
        String firstname = "";
        if(req.getParameter("surname")!=null){
            surname = req.getParameter("surname").trim() + "%";
        }
        if(req.getParameter("firstname")!=null){
            firstname = req.getParameter("firstname").trim() + "%";
        }
        List<User> users = userService.findUsersByName(surname, firstname);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(User.class, new UserSerializer());
        resp.getWriter().print(builder.create().toJson(users));
        resp.getWriter().flush();
        resp.getWriter().close();
    }





}
