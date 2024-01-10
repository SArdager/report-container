package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Branch;
import kz.kdlolymp.termocontainers.entity.ContainerNote;
import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
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
        return departmentRepository.findDepartmentById(id);
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
        List<Department> departments = new ArrayList<>();
        try{
            departments = manager.createQuery("SELECT d FROM Department d WHERE d.branch.id = :paramBranch " +
                    "ORDER BY d.id ASC", Department.class).setParameter("paramBranch", branchId).getResultList();
        } catch (NoResultException ex){}
        return departments;
    }

    public int getBranchId(int departmentId){
        Department department = findDepartmentById(departmentId);
        int branchId = department.getBranch().getId();
        return branchId;
    }

    public String saveDepartment(Department department){
        String message = "";
        if(department.getId()!=null && department.getId()>0){
            departmentRepository.save(department);
            message = "Название объекта изменено";
        } else {
            if(departmentRepository.findDepartmentByDepartmentNameAndBranchId(department.getDepartmentName(), department.getBranch().getId())!=null){
                message = "Объект с таким названием уже имеется";
            } else {
                departmentRepository.save(department);
                message = "Новый объект создан";
            }
        }
        return message;
    }
}
