package kz.kdlolymp.termocontainers.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="containers")
public class Container implements Serializable {

    @Id
    @Column(columnDefinition = "serial", name = "container_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "container_number")
    private String containerNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="value_id", nullable = false)
    private ContainerValue value;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    @Column(name = "release_date")
    private LocalDateTime releaseDate;
    @Column(name = "is_enable")
    private boolean isEnable;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    public Container() {
    }

    public Integer getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getContainerNumber() { return containerNumber; }

    public void setContainerNumber(String containerNumber) { this.containerNumber = containerNumber; }

    public ContainerValue getValue() {return value;}

    public void setValue(ContainerValue value) {this.value = value;}

    public LocalDateTime getRegistrationDate() { return registrationDate; }

    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public LocalDateTime getReleaseDate() { return releaseDate; }

    public void setReleaseDate(LocalDateTime releaseDate) { this.releaseDate = releaseDate; }

    public boolean isEnable() {return isEnable;}

    public void setEnable(boolean enable) {isEnable = enable;}

    public Department getDepartment() {return department;}

    public void setDepartment(Department department) {this.department = department;}
}
