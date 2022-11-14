package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.repositories.*;
import kz.kdlolymp.termocontainers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
public class EditController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ContainerValueService containerValueService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private TimeStandardRepository standardRepository;
    @Autowired
    private TimeStandardService standardService;
    @Autowired
    private ContainerValueRepository valueRepository;

    private Gson gson = new Gson();
    private String message ="";

    @GetMapping("/admin/edit-company")
    public String loadCompanies(Model model){
        List<Company> companies = companyService.findAll();
        model.addAttribute("companies", companies);
        return "admin/edit-company";
    }

    @PostMapping("/admin/edit-values/edit-standard")
    public void editTimeStandard(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int standardId = 0;
        if(req.getParameter("standardId").length()>0) {
            standardId = Integer.parseInt(req.getParameter("standardId"));
        }
        int firstPointId = Integer.parseInt(req.getParameter("firstPointId"));
        int secondPointId = Integer.parseInt(req.getParameter("secondPointId"));
        int timeStandard = Integer.parseInt(req.getParameter("timeStandard"));
        TimeStandard standard = new TimeStandard();
        standard.setTimeStandard(timeStandard);
        standard.setFirstPointId(firstPointId);
        standard.setSecondPointId(secondPointId);
        if(standardId>0){
            standard.setId(standardId);
        }
        standardService.save(standard);
        if(standardId>0){
            message = "Стандарт времени доставки  изменен";
        } else {
            message = "Новый стандарт времени доставки добавлен";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
    @PostMapping("/admin/edit-company/company")
    public void changeCompany(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String companyName = req.getParameter("companyName");
        Company newCompany = new Company();
        newCompany.setCompanyName(companyName);
        if(id>1) {
            newCompany.setId(id);
            companyRepository.save(newCompany);
            message = "Название предприятия изменено";
        } else {
            if(companyService.addCNewCompany(newCompany)){
                message = "Новое предприятие создано";
            } else {
                message = "Имеется предприятие с таким названием";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-company/branch")
    public void changeBranch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        int companyId = Integer.parseInt(req.getParameter("companyId"));
        String branchName = req.getParameter("branchName");

        Company company = companyRepository.findCompanyById(companyId);
        Branch newBranch = new Branch();
        newBranch.setBranchName(branchName);
        newBranch.setCompany(company);
        if(id>1) {
            newBranch.setId(id);
            branchRepository.save(newBranch);
            message = "Название филиала изменено";
        } else {
            if(branchService.addNewBranch(newBranch)){
                message = "Новый филиал создан";
            } else {
                message = "Имеется филиал с таким названием";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-company/department")
    public void changeDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        int branchId = Integer.parseInt(req.getParameter("branchId"));
        String departmentName = req.getParameter("departmentName");

        Branch branch = branchRepository.findBranchById(branchId);
        Department newDepartment = new Department();
        if(id>1) {
            newDepartment.setId(id);
        }
        newDepartment.setDepartmentName(departmentName);
        newDepartment.setBranch(branch);
        departmentRepository.save(newDepartment);
        if(id>0){
            message = "Название объекта изменено";
        } else {
            message = "Новый объект создан";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-company/delete-company")
    public void deleteCompany(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        if(companyService.deleteCompany(id)){
            message = "Предприятие удалено";
        } else {
            message = "Предприятие не может быть удалено, так как база данных содержит записи с этим предприятием (объектом предприятия).";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-company/delete-branch")
    public void deleteBranch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Branch branch = new Branch();
        if(branchService.deleteBranch(id)){
            message = "Филиал удален";
        } else {
            message = "Филиал не может быть удален, так как база данных содержит записи с этим филиалом (объектом филиала).";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/edit-company/delete-department")
    public void deleteDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        if(departmentService.deleteDepartment(id)){
            message = "Объект удален";
        } else{
            message = "Объект не может быть удален, так как база данных содержит записи с этим объектом.";
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

}
