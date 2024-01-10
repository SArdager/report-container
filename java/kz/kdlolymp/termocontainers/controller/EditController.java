package kz.kdlolymp.termocontainers.controller;

import com.google.gson.Gson;
import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.repositories.*;
import kz.kdlolymp.termocontainers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    private TimeStandardService standardService;
    @Autowired
    private ContainerValueRepository valueRepository;

    private Gson gson = new Gson();
    private String message ="";

    @PostMapping("/admin/edit-company/company")
    public void changeCompany(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String companyName = req.getParameter("companyName");
        boolean isLabor = Boolean.parseBoolean(req.getParameter("isLabor"));
        Company newCompany = new Company();
        newCompany.setCompanyName(companyName);
        newCompany.setLabor(isLabor);
        if(id>1) {
            newCompany.setId(id);
            companyRepository.save(newCompany);
            message = "Название предприятия изменено";
        } else {
            if(companyService.addNewCompany(newCompany)){
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
        newDepartment.setDepartmentName(departmentName);
        newDepartment.setBranch(branch);
        if(id>0) {
            newDepartment.setId(id);
        }
        message = departmentService.saveDepartment(newDepartment);
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
