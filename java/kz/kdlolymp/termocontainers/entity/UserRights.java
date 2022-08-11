package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_rights")
public class UserRights implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rights_id")
    private Integer id;
    @ManyToOne()
    @JoinColumn(name = "department_id")
    private Department department;
    @Column(name = "rights")
    private String rights;
    @ManyToOne()
    @JoinColumn(name="user_id")
    private User user;

    public UserRights() {}

    public UserRights(Integer id, Department department, String rights, User user) {
        this.id = id;
        this.department = department;
        this.rights = rights;
        this.user = user;
    }

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    public Department getDepartment() { return department; }

    public void setDepartment(Department department) { this.department = department; }

    public String getRights() {return rights;}

    public void setRights(String rights) {this.rights = rights;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

    @Override
    public String toString() {
        return "UserRights{" +
                "id=" + id +
                ", department=" + department +
                ", rights='" + rights + '\'' +
                ", user=" + user +
                '}';
    }
}
