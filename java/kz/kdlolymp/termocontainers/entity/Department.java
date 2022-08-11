package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="departments")
public class Department implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer id;
    @Column(name = "department_name")
    private String departmentName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="branch_id", nullable = false)
    private Branch branch;

    public Department() {
    }

    public Integer getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getDepartmentName() { return departmentName; }

    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Branch getBranch() {return branch;}

    public void setBranch(Branch branch) {this.branch = branch;}

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", departmentName='" + departmentName + '\'' +
                ", branch=" + branch +
                '}';
    }
}
