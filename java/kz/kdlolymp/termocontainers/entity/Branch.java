package kz.kdlolymp.termocontainers.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="branches")
public class Branch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Integer id;
    @Column(name = "branch_name")
    private String branchName;

//    @OneToMany(targetEntity = Department.class, cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "branch")
//    private List<Department> departments;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="company_id", nullable = false)
    private Company company;

    public Branch() {
    }

    public Integer getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getBranchName() { return branchName; }

    public void setBranchName(String branchName) { this.branchName = branchName; }

    public Company getCompany() {return company;}

    public void setCompany(Company company) {this.company = company;}

    @Override
    public String toString() {
        return "Branch{" +
                "id=" + id +
                ", branchName='" + branchName + '\'' +
                ", company =" + company +
                '}';
    }
}
