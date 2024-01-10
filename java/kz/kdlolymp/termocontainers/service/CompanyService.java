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
    public Company findCompanyById(int companyId){
        return companyRepository.findCompanyById(companyId);
    }
    public boolean addNewCompany(Company company){
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

    public boolean deleteCompany(int id){
        Company company = companyRepository.findCompanyById(id);
        List<Branch> branches = company.getBranches();
        boolean isExistNote = false;
        for(Branch branch: branches){
            if(!branchService.deleteBranch(branch.getId())){
                isExistNote = true;
            }
        }
        if(isExistNote){
            return false;
        } else {
            companyRepository.delete(company);
            return true;
        }
    }
}