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
    private ProbeService probeService;
    @Autowired
    private ContainerValueService containerValueService;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private ProbeRepository probeRepository;
    @Autowired
    private TimeStandardRepository standardRepository;
    @Autowired
    private ContainerValueRepository valueRepository;

    private Gson gson = new Gson();
    private String message ="";

    @GetMapping("/admin/change-company")
    public String loadCompanies(Model model){
        List<Company> companies = companyService.findAll();
        model.addAttribute("companies", companies);
        return "admin/change-company";
    }

    @GetMapping("/admin/edit-values")
    public String loadValues(Model model){
        List<Probe> probes = probeService.findAll();
        List<ContainerValue> containerValues = containerValueService.findAll();
        model.addAttribute("probes", probes);
        model.addAttribute("containerValues", containerValues);
        return "admin/edit-values";
    }

    @GetMapping("/admin/edit-time-standards")
    public String loadBranches(Model model){
        List<Probe> probes = probeService.findAll();
        List<Branch> branches = branchService.findAllBySorted();
        model.addAttribute("probes", probes);
        model.addAttribute("branches", branches);
        return "admin/edit-time-standards";
    }

    @PostMapping("/admin/edit-values/edit-standard")
    public void editTimeStandard(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int standardId = Integer.parseInt(req.getParameter("standardId"));
        int firstPointId = Integer.parseInt(req.getParameter("firstPointId"));
        int secondPointId = Integer.parseInt(req.getParameter("secondPointId"));
        int probeId = Integer.parseInt(req.getParameter("probeId"));
        int timeStandard = Integer.parseInt(req.getParameter("timeStandard"));

        TimeStandard standard = new TimeStandard();
        standard.setTimeStandard(timeStandard);
        standard.setFirstPointId(firstPointId);
        standard.setSecondPointId(secondPointId);
        standard.setProbeId(probeId);
        if(standardId>0){
            standard.setId(standardId);
        }
        standardRepository.save(standard);
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
    @PostMapping("/admin/edit-values/edit-container")
    public void changeContainer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String valueName = req.getParameter("valueName");
        ContainerValue newValue = new ContainerValue();
        newValue.setValueName(valueName);
        if(id>1) {
            newValue.setId(id);
            valueRepository.save(newValue);
            message = "Название вида термоконтейнера изменено";
        } else {
            if(containerValueService.addNewValue(newValue)){
                message = "Новый вид термоконтейнера создан";
            } else {
                message = "Имеется термоконтейнера с таким названием";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
    @PostMapping("/admin/edit-values/edit-probe")
    public void changeProbe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String probeName = req.getParameter("probeName");
        Probe newProbe = new Probe();
        newProbe.setProbeName(probeName);
        if(id>1) {
            newProbe.setId(id);
            probeRepository.save(newProbe);
            message = "Название пробы изменено";
        } else {
            if(probeService.addNewProbe(newProbe)) {
                message = "Новый вид пробы создан";
            } else {
                message = "Имеется вид пробы с таким названием";
            }
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
    @PostMapping("/admin/change-company/company")
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

    @PostMapping("/admin/change-company/branch")
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

    @PostMapping("/admin/change-company/department")
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

    @PostMapping("/admin/edit-values/delete-probe")
    public void deleteProbe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        probeService.deleteProbe(id);
        message = "Вид пробы удален";
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
    @PostMapping("/admin/edit-values/delete-container-value")
    public void deleteContainerValue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        containerValueService.deleteContainerValue(id);
        message = "Вид термоконтейнера удален";
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/change-company/delete-company")
    public void deleteCompany(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        companyService.deleteCompany(id);
        message = "Предприятие удалено";
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/change-company/delete-branch")
    public void deleteBranch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Branch branch = new Branch();
        branchService.deleteBranch(id);
        message = "Филиал удален";
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @PostMapping("/admin/change-company/delete-department")
    public void deleteDepartment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        departmentService.deleteDepartment(id);
        message = "Объект удален";
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

}
