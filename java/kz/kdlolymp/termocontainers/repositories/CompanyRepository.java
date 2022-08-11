package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.Branch;
import kz.kdlolymp.termocontainers.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Company findCompanyById (int companyId);
    Company findCompanyByCompanyName (String companyName);
    List<Company> findAll();
}
