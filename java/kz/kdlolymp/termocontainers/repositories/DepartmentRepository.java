package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Department findDepartmentById (int id);
    Department findDepartmentByDepartmentNameAndBranchId (String departmentName, int branchId);
    List<Department> findAllByBranchId(int branchId);



}
