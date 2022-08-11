package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Branch;
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

    public Department findDepartmentById(int id){
        Department departmentFromDb = departmentRepository.findDepartmentById(id);
        return departmentFromDb;
    }

    public void deleteDepartment(int id){
        Department department = departmentRepository.findDepartmentById(id);
        departmentRepository.delete(department);
    }

    public List<Department> findAllByBranchId(int branchId){
        return departmentRepository.findAllByBranchId(branchId);
    }
}
