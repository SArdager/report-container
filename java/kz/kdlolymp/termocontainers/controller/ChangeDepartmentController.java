package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.controller.serializers.BranchSerializer;
import kz.kdlolymp.termocontainers.controller.serializers.DepartmentSerializer;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
public class ChangeDepartmentController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private UserRightsService userRightsService;
    @Autowired
    private DepartmentService departmentService;
    private Gson gson = new GsonBuilder().create();
    private String message;

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

    @PostMapping("/user/choose-branch")
    public void chooseBranch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getUserFromAuthentication();
        if(user!=null) {
            String preferences = user.getMemory();
            ArrayList<Integer> branchMemory = new ArrayList();
            if (preferences != null && preferences.length() > 0) {
                if (preferences.indexOf("branch") > 0) {
                    preferences = preferences.substring(preferences.indexOf("branch") + 8, preferences.indexOf("}"));
                    while (preferences.length() > 0) {
                        int branchId = Integer.parseInt(preferences.substring(0, preferences.indexOf("[")));
                        preferences = preferences.substring(preferences.indexOf("]") + 2);
                        branchMemory.add(branchId);
                    }
                }
            }
            List<Branch> branches = branchService.findAllLaborSorted();
            message = "";
            for (Branch branch : branches) {
                boolean isFound = false;
                if (branchMemory != null && branchMemory.size() > 0) {
                    for (int mem : branchMemory) {
                        if (mem == branch.getId()) {
                            isFound = true;
                            message += "<tr id='tr_" + branch.getId() + "'><td class='parameter'>" + branch.getBranchName() +
                                    "</td><td class='box_cell'><input type='checkbox' id='br_" + branch.getId() +
                                    "' class='branch_box' checked='checked' /></td></tr>";
                        }
                    }
                    if (!isFound) {
                        message += "<tr id='tr_" + branch.getId() + "'><td class='parameter'>" + branch.getBranchName() +
                                "</td><td class='box_cell'><input type='checkbox' id='br_" + branch.getId() +
                                "' class='branch_box' /></td></tr>";
                    }
                } else {
                    message += "<tr id='tr_" + branch.getId() + "'><td class='parameter'>" + branch.getBranchName() +
                            "</td><td class='box_cell'><input type='checkbox' id='br_" + branch.getId() +
                            "' class='branch_box' checked='checked' /></td></tr>";
                }
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/choose-department")
    public void chooseDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getUserFromAuthentication();
        if(user!=null) {
            int branchId = Integer.parseInt(req.getParameter("branchId"));
            String preferences = user.getMemory();
            boolean isNoFound = true;
            ArrayList<Integer> departmentMemory = new ArrayList();
            if (preferences != null && preferences.length() > 0) {
                if (preferences.indexOf("branch") > 0) {
                    preferences = preferences.substring(preferences.indexOf("branch") + 8, preferences.indexOf("}"));
                    while (preferences.length() > 0 && isNoFound) {
                        int currentBranchId = Integer.parseInt(preferences.substring(0, preferences.indexOf("[")));
                        if (currentBranchId == branchId) {
                            isNoFound = false;
                            String pref = preferences.substring(preferences.indexOf("[") + 1, preferences.indexOf("]"));
                            while (pref.length() > 0) {
                                int departmentId = Integer.parseInt(pref.substring(0, pref.indexOf(",")));
                                pref = pref.substring(pref.indexOf(",") + 1);
                                departmentMemory.add(departmentId);
                            }
                        }
                        preferences = preferences.substring(preferences.indexOf("]") + 2);
                    }
                }
            }
            List<Department> departments = departmentService.findAllByBranchId(branchId);
            message = "";
            for (Department department : departments) {
                if (departmentMemory != null && departmentMemory.size() > 0) {
                    isNoFound = true;
                    for (int dep : departmentMemory) {
                        if (dep == department.getId()) {
                            isNoFound = false;
                            message += "<tr id='trd_" + department.getId() + "'><td class='parameter'>" + department.getDepartmentName() +
                                    "</td><td class='box_cell'><input type='checkbox' id='dep_" + department.getId() +
                                    "' class='dep_box' checked='checked'/></td></tr>";
                        }
                    }
                    if (isNoFound) {
                        message += "<tr id='trd_" + department.getId() + "'><td class='parameter'>" + department.getDepartmentName() +
                                "</td><td class='box_cell'><input type='checkbox' id='dep_" + department.getId() +
                                "' class='dep_box' /></td></tr>";
                    }
                } else {
                    message += "<tr id='trd_" + department.getId() + "'><td class='parameter'>" + department.getDepartmentName() +
                            "</td><td class='box_cell'><input type='checkbox' id='dep_" + department.getId() +
                            "' class='dep_box' checked='checked'/></td></tr>";
                }
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/load-departments")
    public void loadDepartments(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        message = "";
        Long userId = Long.parseLong(req.getParameter("userId"));
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        if(userId>0 && branchId>1) {
            List<UserRights> rightsList = userRightsService.findRightsByUser(userId);
            List<Department> departments = departmentService.findAllByBranchId(branchId);
            boolean isEditor;
            for (Department department : departments) {
                isEditor = false;
                for(UserRights userRights : rightsList) {
                    if (userRights.getDepartment().getId() == department.getId() && userRights.getRights().equals("editor")) {
                        isEditor = true;
                    }
                }
                if (isEditor) {
                    message += "<tr id='trd_" + department.getId() + "'><td style='width:300dp'>" + department.getDepartmentName() +
                            "</td><td style='width:20dp'><input type='checkbox' name='department_boxes' id='dep_" + department.getId() +
                            "' checked='checked'/></td></tr>";
                } else {
                    message += "<tr id='trd_" + department.getId() + "'><td style='width:300dp'>" + department.getDepartmentName() +
                            "</td><td style='width:20dp'><input type='checkbox' name='department_boxes' id='dep_" + department.getId() +
                            "'/></td></tr>";
                }
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/save-preferences")
    public void savePreferences(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getUserFromAuthentication();
        if(user!=null) {
            String preferences = createPreferences(req, user);
            user.setMemory(preferences);
            if (userService.saveUser(user)) {
                if(req.getParameter("branchPref").length()>0){
                    message = "Настройки пользователя по отображаемым филиалам и списку объектов отмеченного филиала сохранены. <br>Списки объектов для других филиалов не меняются.";
                } else {
                    message = "Настройки пользователя по списку объектов отмеченного филиала сохранены. <br>Список отображаемых филиалов не изменен.";
                }
            } else {
                message = "Ошибка записи в базу данных. Перегрузите страницу.";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    private String createPreferences(HttpServletRequest req, User user) {
        String branchPref = req.getParameter("branchPref");
        String depPref = req.getParameter("depPref");
        String branchId = req.getParameter("branchId");
        String departmentId = "";
        if(req.getParameter("departmentId").length() >0){
            departmentId  = req.getParameter("departmentId");
        }
        String preferences = user.getMemory();
        String newPref = "branch {";
        String oldPrefString = "";
        if(preferences!=null && preferences.length()>0) {
            if (preferences.indexOf("branch") > -1) {
                newPref = preferences.substring(0, preferences.indexOf("branch")).trim() + " branch {";
                oldPrefString = preferences.substring(preferences.indexOf("branch") + 8, preferences.length() - 1).trim();
            } else {
                newPref = preferences.trim() + " branch {";
            }
        }
        String departments = "";
        if(depPref.length()>0) {
            depPref = depPref.substring(0, depPref.length() - 1);
            String[] depList = depPref.split(",");
            if(departmentId.length()>0){
                departments += departmentId + ",";
            }
            for(String dep: depList){
                if(!dep.equals(departmentId)){
                    departments += dep + ",";
                }
            }
        }

        ArrayList branches = new ArrayList<>();
        if(branchPref.length()>0){
            branchPref = branchPref.substring(0, branchPref.length() - 1);
            String[] branchList = branchPref.split(",");
            branches.add(branchId);
            for(int i=0; i<branchList.length; i++){
                if(!branchList[i].equals(branchId)){
                    branches.add(branchList[i]);
                }
            }
            newPref.trim();
            newPref += branchId + "[" + departments + "] ";
            if (oldPrefString.length() > 0 && oldPrefString.indexOf("[") > 0) {
                int next = oldPrefString.indexOf("[");
                while (next > 0) {
                    String nextBranch = oldPrefString.substring(0, next);
                    for (int j = 0; j < branches.size(); j++) {
                        if (branches.get(j).equals(nextBranch)) {
                            newPref += oldPrefString.substring(oldPrefString.indexOf("] ") + 1) + " ";
                            branches.remove(j);
                            break;
                        }
                    }
                    if(oldPrefString.length() > oldPrefString.indexOf("] ") + 2) {
                        oldPrefString = oldPrefString.substring(oldPrefString.indexOf("] ") + 2);
                    } else {
                        oldPrefString = "";
                    }
                    if(oldPrefString.length() > 0) {
                        next = oldPrefString.indexOf("[");
                    } else {
                        next = -1;
                    }
                }
            }
            if(branches.size() >0) {
                for (int other = 0; other < branches.size(); other++) {
                    newPref += branches.get(other) + "[] ";
                }
            }
        } else {
            if(oldPrefString.length()>0){
                int index = oldPrefString.indexOf(branchId + "[");
                if(index > -1){
                    int end = oldPrefString.indexOf("]", index);
                    newPref += oldPrefString.substring(0, index) + branchId + "[" + departments + oldPrefString.substring(end) + " ";
                } else {
                    newPref += oldPrefString + " " + branchId + "[" + departments + "] ";
                }
            } else {
                newPref += branchId + "[" + departments + "] ";
            }
        }
        newPref += "}";

        while(newPref.indexOf("  ") >-1) {
            newPref.replace("  ", " ");
        }
        return newPref;
    }
    @PostMapping("/user/save-prefDep")
    public void savePrefDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getUserFromAuthentication();
        if(user!=null) {
            String preferences = createPreferences(req, user);
            user.setMemory(preferences);
            if (userService.saveUser(user)) {
                if(req.getParameter("branchPref").length()>0){
                    message = "Настройки пользователя по отображаемым филиалам и списку объектов отмеченного филиала сохранены. <br>Списки объектов для других филиалов не меняются.";
                } else {
                    message = "Настройки пользователя по списку объектов отмеченного филиала сохранены. <br>Список отображаемых филиалов не изменен.";
                }
            } else {
                message = "Ошибка записи в базу данных. Перегрузите страницу.";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @GetMapping("/user/change-department")
    public String viewChangeDepartment(Model model, HttpServletRequest req) {
        String errorMessage = "";
        if(req.getParameter("errorMessage")!=null){
            errorMessage = (String)req.getParameter("errorMessage");
        }
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
        List<Company> companies = companyService.findAll();
        Department department = departmentService.findDepartmentById(user.getDepartmentId());
        model.addAttribute("user", user);
        model.addAttribute("department", department);
        model.addAttribute("companies", companies);
        model.addAttribute("errorMessage", errorMessage);
        return "user/change-department";
    }

    @PostMapping("/user/change-department/select-company")
    public void changeBranch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int companyId = Integer.parseInt(req.getParameter("companyId"));
        List<Branch> branches = branchService.findAllByCompanyId(companyId);
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Branch.class, new BranchSerializer())
                .create();
        resp.getWriter().print(gson.toJson(branches));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/change-department/select-branch")
    public void changeDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        List<Department> departments = new ArrayList<>();
        if(req.getParameter("branchId").length()>0) {
            int branchId = Integer.parseInt(req.getParameter("branchId"));
            departments = departmentService.findAllByBranchId(branchId);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Department.class, new DepartmentSerializer())
                .create();
        resp.getWriter().print(gson.toJson(departments));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/change-department/select-pref")
    public void loadDepartmentPreferences(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        List<Department> departments = new ArrayList<>();
        if(req.getParameter("branchId")!=null && req.getParameter("branchId").length()>0) {
            int branchId = Integer.parseInt(req.getParameter("branchId"));
            List<Department> departmentsDb = departmentService.findAllByBranchId(branchId);
            User user = getUserFromAuthentication();
            if(user!=null) {
                String preferences = user.getMemory();
                if(preferences!=null && preferences.indexOf("branch")>0){
                    int index = preferences.indexOf(branchId + "[");
                    if(index>-1){
                        int start = preferences.indexOf("[", index) + 1;
                        int end = preferences.indexOf("]", index);
                        String depString = preferences.substring(start, end);
                        if(depString.length() > 0){
                            depString = depString.substring(0, depString.length()-1);
                            String[] depSet = depString.split(",");
                            for(int i=0; i<depSet.length; i++){
                                int id = Integer.parseInt(depSet[i]);
                                for(Department department: departmentsDb){
                                    if(department.getId() == id){
                                        departments.add(department);
                                        break;
                                    }
                                }
                            }
                        } else {
                            departments = departmentsDb;
                        }
                    } else {
                        departments = departmentsDb;
                    }
                } else {
                    departments = departmentsDb;
                }
            }
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        gson = new GsonBuilder()
                .registerTypeAdapter(Department.class, new DepartmentSerializer())
                .create();
        resp.getWriter().print(gson.toJson(departments));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/change-department/choose-department")
    public RedirectView chooseDepartment(HttpServletRequest req, HttpServletResponse resp, Model model, RedirectAttributes attributes) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int departmentId = Integer.parseInt(req.getParameter("departmentId"));
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
        List<UserRights> userRightsList = user.getUserRightsList();
        boolean isAllowed = false;
        String rights = "";
        for(int i=0; i<userRightsList.size(); i++){
            UserRights userRights = userRightsList.get(i);
            if(userRights.getDepartment().getId()==departmentId){
                rights = userRights.getRights();
                isAllowed = true;
            }
        }
        if(isAllowed){
            user.setDepartmentId(departmentId);
            Department department = departmentService.findDepartmentById(departmentId);
            int branchId = department.getBranch().getId();
            user.setBranchId(branchId);
            userService.saveUser(user);
            UserRights userRights = new UserRights();
            if(rights.equals("editor")){
                rights ="ВНЕСЕНИЕ ЗАПИСЕЙ";
            } else if(rights.equals("reader")){
                rights ="ПРОСМОТР ЗАПИСЕЙ";
            } else if(rights.equals("courier")){
                rights ="ДОСТАВКА";
            } else if(rights.equals("changer")){
                rights ="ПРОСМОТР ЖУРНАЛОВ И ИЗМЕНЕНИЕ СРОКОВ ДОСТАВКИ";
            } else if(rights.equals("righter")){
                rights ="ПРОСМОТР ЖУРНАЛОВ И ИЗМЕНЕНИЕ ПРАВ";
            } else if(rights.equals("chef")){
                rights ="ПОЛНЫЕ ПРАВА ПО ЛАБОРАТОРИИ";
            } else {
                rights ="УЧЕТ ТЕРМОКОНТЕЙНЕРОВ";
            }
            userRights.setRights(rights);
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("userRights", userRights);
            return new RedirectView("../../work-starter");

        } else {
            String errorMessage = "Отсутствуют права редактирования или просмотра в этом объекте";
            attributes.addFlashAttribute("flashAttribute", "user/change-department/choose-department");
            attributes.addAttribute("errorMessage", errorMessage);
            return new RedirectView("../change-department");
        }
    }

}
