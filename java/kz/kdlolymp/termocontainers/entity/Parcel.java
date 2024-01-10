package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="parcels")
public class Parcel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parcel_id")
    private Long id;

    @Column(name = "parcel_number")
    private String parcelNumber;

    @Column(name = "dimensions")
    private String dimensions;

    @Column(name = "parcel_note")
    private String note;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "information")
    private boolean information;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_user_id")
    private User createUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "send_user_id")
    private User sendUser;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="department_out_id")
    private Department outDepartment;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name="destination")
    private Department destination;

    @Column(name = "parent_number")
    private String parentNumber;

    @Column(name = "payment")
    private Long payment;

    @Column(name = "costs_part")
    private int costsPart;

    @Column(name = "is_delivered")
    private boolean isDelivered;

    @Column(name = "is_waited")
    private boolean isWaited;

    @Column(name = "current_id")
    private int currentDepartmentId;

    @OneToMany(targetEntity = ParcelPoint.class, cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, mappedBy = "parcelId")
    private List<ParcelPoint> parcelPoints;

    public Parcel() {}

    public Parcel(LocalDateTime createDate, User createUser, Department outDepartment,
                  Department destination, User sendUser, List<ParcelPoint> parcelPoints) {
        this.createDate = createDate;
        this.createUser = createUser;
        this.outDepartment = outDepartment;
        this.destination = destination;
        this.parcelPoints = parcelPoints;
        this.sendUser = sendUser;
    }

    public void addParcelPoint(ParcelPoint point) { this.parcelPoints.add(point);}

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getParcelNumber() {return parcelNumber;}

    public void setParcelNumber(String parcelNumber) {this.parcelNumber = parcelNumber;}

    public String getDimensions() {return dimensions;}

    public void setDimensions(String dimensions) {this.dimensions = dimensions;}

    public LocalDateTime getCreateDate() {return createDate;}

    public void setCreateDate(LocalDateTime createDate) {this.createDate = createDate;}

    public LocalDateTime getSendDate() {return sendDate;}

    public void setSendDate(LocalDateTime sendDate) {this.sendDate = sendDate;}

    public LocalDateTime getDeliveryDate() {return deliveryDate;}

    public void setDeliveryDate(LocalDateTime deliveryDate) {this.deliveryDate = deliveryDate;}

    public boolean isInformation() {return information;}

    public void setInformation(boolean information) {this.information = information;}

    public User getCreateUser() {return createUser;}

    public void setCreateUser(User createUser) {this.createUser = createUser;}

    public User getSendUser() {return sendUser;}

    public void setSendUser(User sendUser) {this.sendUser = sendUser;}

    public Department getOutDepartment() {return outDepartment;}

    public void setOutDepartment(Department outDepartment) {this.outDepartment = outDepartment;}

    public Department getDestination() {return destination;}

    public void setDestination(Department destination) {this.destination = destination;}

    public String getNote() {return note;}

    public void setNote(String note) {this.note = note;}

    public String getParentNumber() {return parentNumber;}

    public void setParentNumber(String parentNumber) {this.parentNumber = parentNumber;}

    public int getCostsPart() {return costsPart;}

    public void setCostsPart(int costsPart) {this.costsPart = costsPart;}

    public boolean isDelivered() {return isDelivered;}

    public void setDelivered(boolean delivered) {isDelivered = delivered;}

    public List<ParcelPoint> getParcelPoints() {return parcelPoints;}

    public void setParcelPoints(List<ParcelPoint> parcelPoints) {this.parcelPoints = parcelPoints;}

    public Long getPayment() {return payment;}

    public void setPayment(Long payment) {this.payment = payment;}

    public boolean isWaited() {return isWaited;}

    public void setWaited(boolean waited) {isWaited = waited;}

    public int getCurrentDepartmentId() {return currentDepartmentId;}

    public void setCurrentDepartmentId(int currentDepartmentId) {this.currentDepartmentId = currentDepartmentId;}
}
