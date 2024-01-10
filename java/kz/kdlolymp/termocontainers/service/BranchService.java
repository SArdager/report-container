package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Branch;
import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.repositories.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
public class BranchService {
    @Autowired
    private BranchRepository branchRepository;
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
    public boolean save(Branch branch){
        branchRepository.save(branch);
        return true;
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
    public List<Branch> findAllSorted(){
        List<Branch> branches = manager.createQuery("SELECT b FROM Branch b WHERE b.id>1 ORDER BY b.id ASC", Branch.class)
                .setMaxResults(30).getResultList();
        return branches;
    }

    public List<Branch> findAllLaborSorted(){
        List<Branch> branchList = manager.createQuery("SELECT b FROM Branch b WHERE b.company.isLabor = true " +
                        "ORDER BY b.branchName ASC", Branch.class).getResultList();
        List<Branch> branches = new ArrayList<>();
        for(int i=0; i<branchList.size(); i++){
            String branchName = branchList.get(i).getBranchName();
            if(branchName.indexOf("г.")==0){
                branches.add(branchList.get(i));
                branchList.remove(branchList.get(i));
                i--;
            }
        }
        for(Branch branch: branchList){
            branches.add(branch);
        }
        return branches;
    }

}
