package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Branch;
import kz.kdlolymp.termocontainers.entity.Company;
import kz.kdlolymp.termocontainers.repositories.BranchRepository;
import kz.kdlolymp.termocontainers.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchService branchService;
    @Autowired
    private EntityManager manager;

    public Company findCompanyByCompanyName(String companyName){
        return companyRepository.findCompanyByCompanyName(companyName);
    }
    public boolean addCNewCompany(Company company){
        Company companyFromDb = findCompanyByCompanyName(company.getCompanyName());
        if(companyFromDb!=null){
            return  false;
        } else {
            companyRepository.save(company);
            return true;
        }
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }
    public void deleteCompany(int id){
        Company company = companyRepository.findCompanyById(id);
        List<Branch> branches = company.getBranches();
        for(Branch branch: branches){
            branchService.deleteBranch(branch.getId());
        }
        companyRepository.delete(company);
    }
}