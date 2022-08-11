package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.service.BranchService;
import kz.kdlolymp.termocontainers.service.CompanyService;
import kz.kdlolymp.termocontainers.service.DepartmentService;
import kz.kdlolymp.termocontainers.service.UserService;
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
import org.springframework.web.bind.annotation.ResponseBody;
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
    private DepartmentService departmentService;
    private Gson gson = new GsonBuilder().create();

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
        List<Branch> branchesDB = branchService.findAllByCompanyId(companyId);
        List<Branch> branches = new ArrayList<>();
        for(Branch branchDB: branchesDB){
            Branch branch = new Branch();
            branch.setId(branchDB.getId());
            branch.setBranchName(branchDB.getBranchName());
            branches.add(branch);
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(branches));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/user/change-department/select-branch")
    public void changeDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        List<Department> departmentsDB = departmentService.findAllByBranchId(branchId);
        List<Department> departments = new ArrayList<>();
        if(departmentsDB.size()>0) {
            for (Department departmentDB : departmentsDB) {
                Department department = new Department();
                department.setId(departmentDB.getId());
                department.setDepartmentName(departmentDB.getDepartmentName());
                departments.add(department);
            }
        }
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(this.gson.toJson(departments));
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
            userService.saveUser(user);
            UserRights userRights = new UserRights();
            if(rights.equals("editor")){
                rights ="ВНЕСЕНИЕ ЗАПИСЕЙ";
            } else {
                rights = "ПРОСМОТР ЗАПИСЕЙ";
            }
            userRights.setRights(rights);
            Department department = departmentService.findDepartmentById(departmentId);
            model.addAttribute("user", user);
            model.addAttribute("department", department);
            model.addAttribute("userRights", userRights);
            return new RedirectView("/user/check-journal");

        } else {
            String errorMessage = "Отсутствуют права ректирования или просмотра в этом объекте";
            attributes.addFlashAttribute("flashAttribute", "user/change-department/choose-department");
            attributes.addAttribute("errorMessage", errorMessage);
            return new RedirectView("/user/change-department");
        }
    }

}
