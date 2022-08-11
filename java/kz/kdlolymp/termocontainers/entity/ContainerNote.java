package kz.kdlolymp.termocontainers.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="container_notes")
public class ContainerNote implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "note_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="container_id", nullable = false)
    private Container container;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="department_out_id")
    private Department outDepartment;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="department_to_id")
    private Department toDepartment;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_to_id")
    private User toUser;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_out_id")
    private User outUser;
    @Column(name = "send_time")
    private LocalDateTime sendTime;
    @Column(name = "arrive_time")
    private LocalDateTime arriveTime;
    @Column(name = "time_standard")
    private int timeStandard;
    @Column(name = "delay_time")
    private Long delayTime;
    @Column(name = "send_note")
    private String sendNote;
    @Column(name = "send_pay")
    private Long sendPay;
    @Column(name = "arrive_note")
    private String arriveNote;
    @Column(name = "delay_note")
    private String delayNote;
    @Column(name = "is_send")
    private boolean isSend;
    @OneToMany(targetEntity = BetweenPoint.class, cascade = CascadeType.ALL,
        fetch = FetchType.LAZY, mappedBy = "containerNoteId")
    private List<BetweenPoint> betweenPoints;

    public ContainerNote() { }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Container getContainer() {return container;}

    public void setContainer(Container container) {this.container = container;}

    public Department getOutDepartment() {return outDepartment;}

    public void setOutDepartment(Department outDepartment) {this.outDepartment = outDepartment;}

    public Department getToDepartment() {return toDepartment;}

    public void setToDepartment(Department toDepartment) {this.toDepartment = toDepartment;}

    public User getToUser() {return toUser;}

    public void setToUser(User toUser) {this.toUser = toUser;}

    public User getOutUser() {return outUser;}

    public void setOutUser(User outUser) {this.outUser = outUser;}

    public LocalDateTime getSendTime() { return sendTime; }

    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }

    public LocalDateTime getArriveTime() { return arriveTime; }

    public void setArriveTime(LocalDateTime arriveTime) { this.arriveTime = arriveTime; }

    public int getTimeStandard() { return timeStandard; }

    public void setTimeStandard(int timeStandard) { this.timeStandard = timeStandard; }

    public Long getDelayTime() {return delayTime;}

    public void setDelayTime(Long delayTime) {this.delayTime = delayTime;}


    public String getSendNote() {return sendNote;}

    public void setSendNote(String sendNote) {this.sendNote = sendNote;}

    public Long getSendPay() {return sendPay;}

    public void setSendPay(Long sendPay) {this.sendPay = sendPay;}

    public String getArriveNote() {return arriveNote;}

    public void setArriveNote(String arriveNote) {this.arriveNote = arriveNote;}

    public String getDelayNote() {return delayNote;}

    public void setDelayNote(String delayNote) {this.delayNote = delayNote;}

    public boolean isSend() {return isSend;}

    public void setSend(boolean send) {isSend = send;}

    public List<BetweenPoint> getBetweenPoints() {return betweenPoints;}

    public void setBetweenPoints(List<BetweenPoint> betweenPoints) {this.betweenPoints = betweenPoints;}
}
