package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Branch;
import kz.kdlolymp.termocontainers.entity.Company;
import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.repositories.BranchRepository;
import kz.kdlolymp.termocontainers.repositories.CompanyRepository;
import kz.kdlolymp.termocontainers.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class BranchService {
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private  ContainerNoteService containerNoteService;
    @Autowired
    private  DepartmentService departmentService;
    @PersistenceContext
    private EntityManager manager;

    public Branch findBranchById(int id){
        return branchRepository.findBranchById(id);
    }
    public Branch findBranchByBranchName(String branchName){
        return branchRepository.findBranchByBranchName(branchName);
    }

    public boolean addNewBranch(Branch branch){
        Branch branchFromDb = findBranchByBranchName(branch.getBranchName());
        if(branchFromDb!=null){
            return false;
        } else {
            branchRepository.save(branch);
            return true;
        }
    }
    public List<Branch> findAllByCompanyId(int companyId){
        return branchRepository.findByCompanyId(companyId);
    }
    public boolean deleteBranch(int id){
        Branch branch = branchRepository.findBranchById(id);
        List<Department> departments = departmentService.findAllByBranchId(branch.getId());
        if(departments!=null && departments.size()>0) {
            boolean isNoteExist = false;
            for (Department department : departments) {
                if (containerNoteService.checkNotesByDepartmentId(department.getId())) {
                    isNoteExist = true;
                }
            }
            if (isNoteExist) {
                return false;
            } else {
                for (Department department : departments) {
                    departmentService.deleteDepartment(department.getId());
                }
                branchRepository.delete(branch);
                return true;
            }
        } else{
            branchRepository.delete(branch);
            return true;
        }
    }
    public List<Branch> findAllBySorted(){
        List<Branch> dBbranches = manager.createQuery("SELECT b FROM Branch b", Branch.class)
                .setMaxResults(30).getResultList();
        List<Branch> branches = new ArrayList<>();
        for(int i=0; i<dBbranches.size(); i++){
            Branch branch = dBbranches.get(i);
            if(branch.getId()==1){
                dBbranches.remove(i);
            }
            if(branch.getBranchName().indexOf("филиал") < 2){
                branches.add(branch);
                dBbranches.remove(i);
            }
        }
        Collections.sort(dBbranches, new Comparator<Branch>() {
            @Override
            public int compare(Branch o1, Branch o2) {
                return o1.getBranchName().compareTo(o2.getBranchName());
            }
        });
        for(int j=0; j<dBbranches.size(); j++){
            branches.add(dBbranches.get(j));
        }
        return branches;
    }

}
