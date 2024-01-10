package kz.kdlolymp.termocontainers.entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="parcel_points")
public class ParcelPoint implements Serializable, Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parcel_point_id")
    private Long id;

   @Column(name = "parcel_id")
    private Long parcelId;

   @Column(name = "parcel_number")
    private String parcelNumber;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="to_department_id")
    private Department toDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "into_user_id")
    private User intoUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "out_user_id")
    private User outUser;

    @Column(name = "arrive_time")
    private LocalDateTime arriveTime;

    @Column(name = "send_time")
    private LocalDateTime sendTime;

    @Column(name = "parent")
    private String parent;
    @Column(name = "text")
    private String text;
    @Column(name = "payment")
    private Long payment;

    public ParcelPoint() {}

    public ParcelPoint(Long parcelId, String parcelNumber, Department department, String parent, Long payment) {
        this.parcelId = parcelId;
        this.parcelNumber = parcelNumber;
        this.department = department;
        this.parent = parent;
        this.payment = payment;
    }

    public ParcelPoint(Long parcelId, String parcelNumber, Department department, User outUser,
                       LocalDateTime sendTime, String text, String parent) {
        this.parcelId = parcelId;
        this.parcelNumber = parcelNumber;
        this.department = department;
        this.outUser = outUser;
        this.sendTime = sendTime;
        this.text = text;
        this.parent = parent;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Long getParcelId() {return parcelId;}

    public void setParcelId(Long parcelId) {this.parcelId = parcelId;}

    public String getParcelNumber() {return parcelNumber;}

    public void setParcelNumber(String parcelNumber) {this.parcelNumber = parcelNumber;}

    public Department getDepartment() {return department;}

    public void setDepartment(Department department) {this.department = department;}

    public Department getToDepartment() {return toDepartment;}

    public void setToDepartment(Department toDepartment) {this.toDepartment = toDepartment;}

    public User getIntoUser() {return intoUser;}

    public void setIntoUser(User intoUser) {this.intoUser = intoUser;}

    public User getOutUser() {return outUser;}

    public void setOutUser(User outUser) {this.outUser = outUser;}

    public LocalDateTime getArriveTime() {return arriveTime;}

    public void setArriveTime(LocalDateTime arriveTime) {this.arriveTime = arriveTime;}

    public LocalDateTime getSendTime() {return sendTime;}

    public void setSendTime(LocalDateTime sendTime) {this.sendTime = sendTime;}

    public String getParent() {return parent;}

    public void setParent(String parent) {this.parent = parent;}

    public String getText() {return text;}

    public void setText(String text) {this.text = text;}

    public Long getPayment() {return payment;}

    public void setPayment(Long payment) {this.payment = payment;}

    @Override
    public int compareTo(Object o) {

        return 0;
    }
}
