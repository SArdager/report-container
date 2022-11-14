package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Branch;
import kz.kdlolymp.termocontainers.entity.ContainerNote;
import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class DepartmentService {

    @PersistenceContext
    private EntityManager manager;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private ContainerNoteService containerNoteService;

    public Department findDepartmentById(int id){
        Department departmentFromDb = departmentRepository.findDepartmentById(id);
        return departmentFromDb;
    }

    public boolean deleteDepartment(int id){
        if(containerNoteService.checkNotesByDepartmentId(id)){
            return false;
        } else {
            Department department = departmentRepository.findDepartmentById(id);
            departmentRepository.delete(department);
            return true;
        }
    }
    public List<Department> findAllByBranchId(int branchId){
        return departmentRepository.findAllByBranchId(branchId);
    }
}
