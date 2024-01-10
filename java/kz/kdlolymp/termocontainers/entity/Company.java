package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="companies")
public class Company implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Integer id;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "is_labor")
    private boolean isLabor;

    @OneToMany(targetEntity = Branch.class, cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, mappedBy = "company")
    private List<Branch> branches;

    public Company() {
    }

    public Integer getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getCompanyName() { return companyName; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public List<Branch> getBranches() {return branches;}

    public boolean isLabor() {return isLabor;}

    public void setLabor(boolean labor) {isLabor = labor;}

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
        for(Branch branch: branches){
            branch.setCompany(this);
        }
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", branches=" + branches +
                '}';
    }
}
