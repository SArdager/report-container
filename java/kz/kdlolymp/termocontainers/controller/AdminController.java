package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.UserSerializer;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private CompanyService companyService;
    @Autowired
    private BranchService branchService;
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
    @Autowired
    private EventLogService eventLogService;

    private Gson gson = new Gson();
    private String message = "";

    @RequestMapping("/admin")
    public String viewAdminStarter(Model model) {
        User user = getUserFromAuthentication();
        if (user != null && user.getRole().equals("ADMIN")) {
            return "admin";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/change-password")
    public String viewChangePassword(Model model) {
        User user = getUserFromAuthentication();
        if (user != null) {
            return "change-password";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/admin/reset-password")
    public String viewResetPassword() {
        User user = getUserFromAuthentication();
        if (user != null && user.getRole().equals("ADMIN")) {
            return "admin/reset-password";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/admin/alarm-groups")
    public String editAlarmGroups(Model model) {
        User user = getUserFromAuthentication();
        if (user != null && user.getRole().equals("ADMIN")) {
            List<AlarmGroup> alarmGroups = alarmGroupService.findAll();
            List<Branch> branches = branchService.findAllLaborSorted();
            model.addAttribute("branches", branches);
            model.addAttribute("alarmGroups", alarmGroups);
            return "admin/alarm-groups";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/admin/edit-user")
    public String viewEditUser() {
        User user = getUserFromAuthentication();
        if (user != null && user.getRole().equals("ADMIN")) {
            return "admin/edit-user";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/admin/info-users")
    public String findUserPage(Model model) {
        User user = getUserFromAuthentication();
        if (user != null && user.getRole().equals("ADMIN")) {
            List<Branch> branches = branchService.findAllSorted();
            model.addAttribute("branches", branches);
            return "admin/info-users";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/admin/edit-company")
    public String loadCompanies(Model model) {
        User user = getUserFromAuthentication();
        if (user != null && user.getRole().equals("ADMIN")) {
            List<Company> companies = companyService.findAll();
            model.addAttribute("companies", companies);
            return "admin/edit-company";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/admin/event-log")
    public String loadEventNames(Model model) {
        User user = getUserFromAuthentication();
        if (user != null && user.getRole().equals("ADMIN")) {
            List<Branch> branches = branchService.findAllSorted();
            model.addAttribute("branches", branches);
            return "admin/event-log";
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

    @RequestMapping("/admin/add-user")
    public String registration(Model model) {
        User user = getUserFromAuthentication();
        if (user != null && user.getRole().equals("ADMIN")) {
            List<Company> companies = companyService.findAll();
            model.addAttribute("user", user);
            model.addAttribute("companies", companies);
            return "admin/add-user";
        } else {
            return "redirect: ../login";
        }
    }

    @RequestMapping("/admin/edit-rights")
    public String rightsEditor(Model model) {
        User user = getUserFromAuthentication();
        if (user != null && user.getRole().equals("ADMIN")) {
            List<Company> companies = companyService.findAll();
            model.addAttribute("companies", companies);
            return "admin/edit-rights";
        } else {
            return "redirect: ../login";
        }
    }

    @PostMapping("/change-password/change")
    public String changePassword(HttpServletRequest req, HttpServletResponse resp, Model model) {
        String password = req.getParameter("password");
        String username = "";
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userService.findByUsername(username);
        user.setPassword(password);
        user.setTemporary(false);
        if (userService.changePassword(user)) {
            return "redirect: ../work-starter";
        } else {
            model.addAttribute("errorChange", "Ошибка смены пароля пользователя");
            return "change-password";
        }

    }

    @PostMapping("/forget-password")
    public void forgetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        User user = userService.findByUsername(username);
        if (user != null && user.isEnabled()) {
            TemporaryPasswordGenerator generator = new TemporaryPasswordGenerator();
            String password = generator.generateTemporaryPassword();
            user.setPassword(password);
            user.setTemporary(true);
            String userName = user.getUserSurname() + user.getUserFirstname();
            if (userService.changePassword(user)) {
                message = "Временный пароль отправлен на ваш адрес электронной почты, указанный при регистрации.";
                String toAddress = user.getEmail();
                Thread sendMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        emailService.sendTemporaryPassword(userName, toAddress, password);
                    }
                });
                sendMessage.start();
            } else {
                message = "Ошибка сброса пароля. <br>Перегрузите страницу позднее или направьте заявку на сброс пароля в службу технической поддержки через сервис-платформу ELMA";
            }
        } else {
            message = "";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-user")
    public void editUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Long id = Long.parseLong(req.getParameter("id"));
        String userSurname = req.getParameter("userSurname");
        String userFirstname = req.getParameter("userFirstname");
        String position = req.getParameter("position");
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        boolean isEnabled = Boolean.parseBoolean(req.getParameter("isEnabled"));
        boolean isLinkSend = Boolean.parseBoolean(req.getParameter("isLinkSend"));
        User user = userService.findUserById(id);
        String oldLogin = user.getUsername();
        String oldEmail = user.getEmail();
        user.setUserSurname(userSurname);
        user.setUserFirstname(userFirstname);
        user.setPosition(position);
        user.setEmail(email);
        user.setUsername(username);
        user.setEnabled(isEnabled);
        if (userService.saveUser(user)) {
            if (isLinkSend) {
                TemporaryPasswordGenerator generator = new TemporaryPasswordGenerator();
                String password = generator.generateTemporaryPassword();
                user.setPassword(passwordEncoder().encode(password));
                user.setTemporary(true);
                userService.saveUser(user);
                if (emailService.sendNewUserMessage(user.getUserSurname() + " " + user.getUserFirstname(),
                        user.getUsername(), email, password)) {
                    message = "Пользователь " + user.getUserSurname() + " " + user.getUserFirstname() + " добавлен." +
                            "<br>Сообщение о регистрации с разовым паролем выслано на адрес электронной почты пользователя, указанный при регистрации.";
                }
            } else if (!oldLogin.equals(username)) {
                TemporaryPasswordGenerator generator = new TemporaryPasswordGenerator();
                String password = generator.generateTemporaryPassword();
                user.setPassword(passwordEncoder().encode(password));
                user.setTemporary(true);
                userService.saveUser(user);
                if (emailService.sendNewLoginMessage(user.getUserSurname() + " " + user.getUserFirstname(),
                        user.getUsername(), email, password)) {
                    message = "Пользователю " + user.getUserSurname() + " " + user.getUserFirstname() + " изменен логин." +
                            "<br>Сообщение об изменении логина с разовым паролем выслано на адрес электронной почты пользователя, указанный при регистрации.";
                }
            } else if (!oldEmail.equals(email)) {
                emailService.sendNewEmailMessage(user.getUserSurname() + " " + user.getUserFirstname(), oldEmail, email);
            } else {
                message = "Данные пользователя *" + user.getUserSurname() + " " + user.getUserFirstname() + "* изменены.";
            }
        } else {
            message = "Ошибка редактирования пользователя";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @GetMapping("/admin/add-user/check-double-user")
    public void checkDoubleUser(HttpServletResponse resp) throws IOException {
        message = "";
        if(getUserFromAuthentication()!=null) {
            List<User> users = userService.getDoubleUsers();
            if (users != null && users.size() > 0) {
                for(User user: users) {
                    String branchName = branchService.findBranchById(user.getBranchId()).getBranchName();
                    message += "<tr><td>" + user.getUserSurname() + "</td><td>" + user.getUserFirstname() + "</td><td>" + user.getId() + "</td><td>" + user.getUsername() +
                            "</td><td>" + user.getEmail() + "</td><td>" + branchName + "</td></tr>";
                }
            } else {
                message += "<tr><td colspan='6'>Дубли пользователей не найдены></td></tr>";
            }
        } else {
            message += "<tr><td colspan='6'><u>Перегрузите страницу></u></td></tr>";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/add-user/save-user")
    public void addUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User userForm = new User();
        List<UserRights> userRightsList = new ArrayList<>();
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        int branchId = department.getBranch().getId();
        userForm.setUsername(req.getParameter("username").trim());
        userForm.setUserSurname(req.getParameter("userSurname").trim());
        userForm.setUserFirstname(req.getParameter("userFirstname").trim());
        userForm.setPosition(req.getParameter("position").trim());
        String email = req.getParameter("email").trim();
        userForm.setEmail(email);
        userForm.setRole(req.getParameter("role"));
        userForm.setDepartmentId(departmentId);
        userForm.setBranchId(branchId);
        TemporaryPasswordGenerator generator = new TemporaryPasswordGenerator();
        String password = generator.generateTemporaryPassword();
        userForm.setPassword(password);
        userForm.setEnabled(true);
        userForm.setTemporary(true);
        userForm.setUserRightsList(userRightsList);
        if (userService.addNewUser(userForm)) {
            User user = userService.findByUsername(userForm.getUsername());
            UserRights userRights = new UserRights();
            userRights.setDepartment(department);
            userRights.setRights(req.getParameter("rights"));
            userRights.setUser(user);
            user.addUserRights(userRights);
            userRightsService.addNewUserRights(userRights);
            if (userService.saveUser(user)) {
                if (emailService.sendNewUserMessage(user.getUserSurname() + " " + user.getUserFirstname(),
                        user.getUsername(), email, password)) {
                    message = "Пользователь " + user.getUserSurname() + " " + user.getUserFirstname() + " добавлен." +
                            "<br>Сообщение о регистрации с разовым паролем выслано на адрес электронной почты пользователя.";
                } else {
                    message = "Пользователь зарегистрирован в системе, но сообщение о регистрации с разовым паролем НЕ БЫЛО ВЫСЛАНО на адрес электронной почты пользователя из-за сбоя работы почтового сервера.";
                }
            } else {
                message = "Сбой при регистрации пользователя. Перегрузите страницу.";
            }
        } else {
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
        if (usersList.size() > 0) {
            for (User userFromList : usersList) {
                User user = new User();
                user.setId(userFromList.getId());
                user.setUsername(userFromList.getUsername());
                user.setUserFirstname(userFromList.getUserFirstname());
                user.setUserSurname(userFromList.getUserSurname());
                user.setDepartmentId(userFromList.getDepartmentId());
                user.setBranchId(userFromList.getBranchId());
                user.setPosition(userFromList.getPosition());
                user.setRole(userFromList.getRole());
                users.add(user);
            }
        }
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(User.class, new UserSerializer());
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(builder.create().toJson(users));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/search-department-users")
    public void searchDepartmentUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        List<User> users = userService.getAllByDepartmentId(departmentId);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(User.class, new UserSerializer());
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(builder.create().toJson(users));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-rights/add-group-rights")
    public void changeGroupRights(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        if (req.getParameter("id") != null) {
            Long userId = Long.parseLong(req.getParameter("id"));
            String rights = req.getParameter("rights");
            User user;
            String departmentString = "";
            if (userId > 0) {
                user = userService.findUserById(userId);
                ArrayList departments = new ArrayList();
                ArrayList deleteDep = new ArrayList();
                while (rights.length() > 0) {
                    int pos = rights.indexOf(" ");
                    if(pos>0) {
                        String dep = rights.substring(0, pos + 1).trim();
                        departments.add(dep);
                        rights = rights.substring(pos + 1);
                    } else {
                        departments.add(rights);
                        rights = "";
                    }
                }
                int number = departments.size();
                ArrayList depForEvents = departments;
                List<UserRights> userRightsList = user.getUserRightsList();
                boolean isFound;
                if (userRightsList != null && userRightsList.size() > 0) {
                    for (int i = 0; i < userRightsList.size(); i++) {
                        isFound = false;
                        UserRights userRights = userRightsList.get(i);
                        int savedDepartmentId = userRights.getDepartment().getId();
                        if (departments.size() > 0) {
                            for (int j = 0; j < departments.size(); j++) {
                                if (Integer.parseInt(departments.get(j).toString()) == savedDepartmentId) {
                                    isFound = true;
                                    userRights.setRights("editor");
                                    Thread changeUserRightsThread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            userRightsService.changeUserRights(userRights);
                                        }
                                    });
                                    changeUserRightsThread.run();
                                    departments.remove(j);
                                    j--;
                                    departmentString += userRights.getDepartment().getDepartmentName() + ", ";
                                }
                            }
                        }
                        if (!isFound) {
                            if (userRights.getRights().equals("editor")) {
                                deleteDep.add(userRights.getDepartment().getId() + "");
                                Thread deleteUserRightsThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        userRightsService.deleteUserRights(userRights);
                                    }
                                });
                                deleteUserRightsThread.run();
                                userRightsList.remove(i);
                                i--;
                            }
                        }
                    }
                }
                if(departments.size()>0){
                    for (int k = 0; k < departments.size(); k++) {
                        UserRights newUserRights = new UserRights();
                        newUserRights.setRights("editor");
                        Department department = departmentService.findDepartmentById(Integer.parseInt(departments.get(k).toString()));
                        newUserRights.setDepartment(department);
                        newUserRights.setUser(user);
                        Thread addUserRightsThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                userRightsService.addNewUserRights(newUserRights);
                            }
                        });
                        addUserRightsThread.run();
                        userRightsList.add(newUserRights);
                        departmentString += department.getDepartmentName() + ", ";
                    }
                }
                user.setUserRightsList(userRightsList);
                if (userService.saveUser(user)) {
                    String userName = user.getUserSurname() + " " + user.getUserFirstname();
                    String email = user.getEmail();
                    if (number > 0) {
                        if (emailService.sendNewRightsMessage(userName, email, departmentString, "Регистрация")) {
                            message = "Пользователю " + userName + " добавлены (изменены) права. <br>" +
                                    "Сообщение об изменении прав отправлено на почтовый адрес пользователя.";
                        } else {
                            message = "Пользователю " + userName + " добавлены (изменены) права.<br>" +
                                    "ВНИМАНИЕ! Из-за сбоя почтовой службы сообщение об изменении прав не было отправлено на почтовый адрес пользователя.";
                        }
                    } else {
                        message = "Пользователю " + userName + " убраны все права регистрации на объектах.";
                    }
                } else {
                    message = "Ошибка изменения прав пользователя";
                }

                User userEditor = getUserFromAuthentication();
                if (userEditor != null) {
                    if (depForEvents.size() > 0) {
                        for (int n=0; n<depForEvents.size(); n++) {
                            int id = Integer.parseInt(depForEvents.get(n).toString());
                            eventLogService.saveEvent(userEditor, id, userId, "внесение и редактирование записей", "групповое изменение", 2);
                        }
                    }
                    if (deleteDep.size() > 0) {
                        for (int m = 0; m < deleteDep.size(); m++) {
                            int id = Integer.parseInt(deleteDep.get(m).toString());
                            eventLogService.saveEvent(userEditor, id, userId, "удалены все права", "внесение и редактирование записей", 2);
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

    @PostMapping("/admin/edit-rights/rights")
    public void changeRights(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User user = new User();
        if (req.getParameter("username") != null && req.getParameter("username").length() > 0) {
            String username = req.getParameter("username");
            user = userService.findByUsername(username);
        } else {
            if (req.getParameter("userId") != null && Long.parseLong(req.getParameter("userId")) > 0) {
                Long userId = Long.parseLong(req.getParameter("userId"));
                user = userService.findUserById(userId);
            }
        }
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
        Department department = departmentService.findDepartmentById(departmentId);
        String rights = req.getParameter("rights");
        boolean isRightsNeedAdd = true;

        String role = "";
        if (req.getParameter("role") != null) {
            role = req.getParameter("role");
        }
        if (role.length() > 0) {
            if (role.equals("ADMIN")) {
                user.setRole("ADMIN");
            } else {
                user.setRole("USER");
            }
        }
        String oldValue = "reset";
        List<UserRights> userRightsList = user.getUserRightsList();
        if (userRightsList != null && userRightsList.size() > 0) {
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
                        i--;
                        userRightsService.deleteUserRights(userRights);
                    }
                    user.setUserRightsList(userRightsList);
                }
            }
        }
        if (isRightsNeedAdd && !rights.equals("reset")) {
            UserRights newUserRights = new UserRights();
            newUserRights.setDepartment(department);
            newUserRights.setRights(rights);
            newUserRights.setUser(user);
            UserRights userRights = userRightsService.addNewUserRights(newUserRights);
            user.addUserRights(userRights);
        }
        String userRights;
        if (rights.equals("reader")) {
            userRights = "просмотр записей и получение отчетов";
        } else if (rights.equals("editor")) {
            userRights = "внесение и редактирование записей";
        } else if (rights.equals("courier")) {
            userRights = "курьер - внесение записей";
        } else if (rights.equals("changer")) {
            userRights = "просмотр записей и изменение срока доставки";
        } else if (rights.equals("righter")) {
            userRights = "просмотр записей и редактирование прав";
        } else if (rights.equals("chef")) {
            userRights = "полные права по лаборатории";
        } else if (rights.equals("account")) {
            userRights = "по учету термоконтейнеров";
        } else if (rights.equals("creator")) {
            userRights = "по созданию и отправке почтовых корреспонденций";
        } else if (rights.equals("reset")) {
            userRights = "убраны права доступа";
        } else {
            userRights = "undefined";
        }

        if (userService.saveUser(user)) {
            String userName = user.getUserSurname() + " " + user.getUserFirstname();
            String email = user.getEmail();
            if (!rights.equals("reset")) {
                String departmentString = department.getDepartmentName() + ", " + department.getBranch().getBranchName();
                if (emailService.sendNewRightsMessage(userName, email, departmentString, userRights)) {
                    message = "Пользователю " + userName + " добавлены (изменены) права. <br>" +
                            "Сообщение об изменении прав отправлено на корпоративный почтовый адрес пользователя.";
                } else {
                    message = "Пользователю " + userName + " добавлены (изменены) права.<br>" +
                            "ВНИМАНИЕ! Из-за сбоя почтовой службы сообщение об изменении прав не было отправлено на корпоративный почтовый адрес пользователя.";
                }
            } else {
                message = "Пользователю " + userName + " убраны права по объекту.";
            }
        } else {
            message = "Ошибка изменения прав пользователя";
        }

        if (oldValue.equals("reader")) {
            oldValue = "просмотр записей и получение отчетов";
        } else if (oldValue.equals("editor")) {
            oldValue = "внесение и редактирование записей";
        } else if (oldValue.equals("courier")) {
            oldValue = "курьер - внесение записей";
        } else if (oldValue.equals("changer")) {
            oldValue = "просмотр записей и изменение срока доставки";
        } else if (oldValue.equals("righter")) {
            oldValue = "просмотр записей и редактирование прав";
        } else if (oldValue.equals("chef")) {
            oldValue = "полные права по лаборатории";
        } else if (oldValue.equals("account")) {
            oldValue = "по учету термоконтейнеров";
        } else if (oldValue.equals("creator")) {
            userRights = "по созданию и отправке почтовых корреспонденций";
        } else if (oldValue.equals("reset")) {
            oldValue = "отсутствуют права";
        } else {
            oldValue = "undefined";
        }
        User userEditor = getUserFromAuthentication();
        if (userEditor != null) {
            eventLogService.saveEvent(userEditor, departmentId, user.getId(), userRights + ", role: " + role, oldValue, 2);
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-alarm-group/change-group")
    public void changeAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(req.getParameter("id"));
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        String alarmGroupName = req.getParameter("alarmGroupName");
        if (id > 0) {
            AlarmGroup alarmGroup = alarmGroupService.findAlarmGroupById(id);
            alarmGroup.setAlarmGroupName(alarmGroupName);
            if (alarmGroupService.saveAlarmGroup(alarmGroup)) {
                message = "Название группы оповещения изменено.";
            } else {
                message = "Ошибка изменения при записи в базу данных. Перегрузите страницу.";
            }
        } else {
            AlarmGroup alarmGroup = alarmGroupService.findAlarmGroupByName(alarmGroupName);
            if (alarmGroup != null) {
                message = "Группа оповещения с таким названием уже имеется. Измените название.";
            } else {
                AlarmGroup newAlarmGroup = new AlarmGroup();
                newAlarmGroup.setAlarmGroupName(alarmGroupName);
                newAlarmGroup.setBranchId(branchId);
                List<User> users = new ArrayList<>();
                newAlarmGroup.setUsers(users);
                if (alarmGroupService.addNewAlarmGroup(newAlarmGroup)) {
                    message = "Создана новая группа оповещения.";
                } else {
                    message = "Ошибка при создании новой группы оповещения. Перегрузите страницу.";
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
    public void deleteAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(req.getParameter("id"));

        if (alarmGroupService.deleteAlarmGroup(id)) {
            message = "Группа оповещения удалена.";
        } else {
            message = "Ошибка удаления группы оповещения, возможно имеются работники в группе оповещения, которых предварительно следует убрать. Перегрузите страницу.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-alarm-group/add-user")
    public void addUserToAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int alarmGroupId = Integer.parseInt(req.getParameter("alarmGroupId"));
        Long userId = Long.parseLong(req.getParameter("userId"));
        User user = userService.findUserById(userId);
        if (alarmGroupService.addNewUserToAlarmGroup(user, alarmGroupId)) {
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
    public void removeUserFromAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int alarmGroupId = Integer.parseInt(req.getParameter("alarmGroupId"));
        Long userId = Long.parseLong(req.getParameter("userId"));
        User user = userService.findUserById(userId);
        if (alarmGroupService.removeUserFromAlarmGroup(user, alarmGroupId)) {
            message = "Пользователь удален из группы оповещения.";
        } else {
            message = "Ошибка удаления пользователя из группы оповещения. Перегрузите страницу.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-alarm-group/get-user-group")
    public void getUsersFromAlarmGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
    public void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Long id = Long.parseLong(req.getParameter("id"));
        User user = userService.findUserById(id);
        TemporaryPasswordGenerator generator = new TemporaryPasswordGenerator();
        String password = generator.generateTemporaryPassword();
        user.setPassword(password);
        user.setTemporary(true);
        String email = user.getEmail();
        String userName = user.getUserSurname() + user.getUserFirstname();
        if (userService.changePassword(user)) {
            if (emailService.sendTemporaryPassword(userName, email, password)) {
                message = "Временный пароль выслан на адрес электронной почты пользователю:  " + user.getUserSurname() + " " + user.getUserFirstname();
            } else {
                message = "ВНИМАНИЕ! Ошибка отправки разового пароля на адрес электронной почты пользователя: " + user.getUserSurname() + " " + user.getUserFirstname() +
                        "<br>Перегрузите страницу сброс пароля позднее или направьте разовый пароль:  " + password + "  пользователю со своего почтового сервиса на адрес: " + email;
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

    @PostMapping("/admin/find-user")
    public void findUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String surname = "";
        String firstname = "";
        String login = "";
        int branchId = 1;
        int departmentId = 1;
        if (req.getParameter("surname") != null) {
            surname = "%" + req.getParameter("surname").trim() + "%";
        }
        if (req.getParameter("firstname") != null) {
            firstname = "%" + req.getParameter("firstname").trim() + "%";
        }
        if (req.getParameter("login") != null) {
            login = "%" + req.getParameter("login").trim() + "%";
        }
        if (req.getParameter("branchId") != null) {
            branchId = Integer.parseInt(req.getParameter("branchId"));
        }
        if (req.getParameter("departmentId") != null) {
            departmentId = Integer.parseInt(req.getParameter("departmentId"));
        }
        List<User> users = new ArrayList<>();
        if(departmentId>1){
            users = userService.findUsersByDepartment(departmentId);
        } else {
            users = userService.findUsersByName(surname, firstname, login, branchId);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(User.class, new UserSerializer());
        resp.getWriter().print(builder.create().toJson(users));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/del-user")
    public void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Long id = Long.parseLong(req.getParameter("id"));
        User user = userService.findUserById(id);
        user.setEnabled(false);
        if (userService.saveUser(user)) {
            message = "Функции пользователя выключены при сохранении всех записей в базе данных.";
        } else {
            message = "Ошибка удаления пользователя";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/find-department")
    public void findDepartment(HttpServletRequest req, HttpServletResponse resp, Model model, RedirectAttributes attributes) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        List<Department> departments = departmentService.findAllByBranchId(branchId);
        int departmentId = 1;

        if (departments != null && departments.size() > 0) {
            int i = -1;
            do {
                i++;
                departmentId = departments.get(i).getId();
            } while (departments.get(i).getDepartmentName().indexOf("борат") < 0 && i < departments.size() - 1);
            if (departments.get(i).getDepartmentName().indexOf("борат") < 0) {
                departmentId = 1;
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(departmentId + "");
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/branch/sql")
    public void sqlRequest(HttpServletRequest req, HttpServletResponse resp, Model model, RedirectAttributes attributes) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        int companyId = Integer.parseInt(req.getParameter("companyId"));
        Branch branch = branchService.findBranchById(branchId);
        Company company = companyService.findCompanyById(companyId);
        branch.setCompany(company);
        if (branchService.save(branch)) {
            message = "Запрос успешно выполнен";
        } else {
            message = "Ошибка обращения в базу данных";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

}
