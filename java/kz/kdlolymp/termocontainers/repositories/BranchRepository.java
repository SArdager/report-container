package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    Branch findBranchById(int branchId);
    Branch findBranchByBranchName(String branchName);
    List<Branch> findByCompanyId(int companyId);
//    @Modifying
//    @Query("UPDATE Branch b SET b.company.id = &paramComId")
}
