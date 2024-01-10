package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="event_logs")
public class EventLog  implements Serializable {
    @Id
    @Column(columnDefinition = "serial", name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "log_time")
    private LocalDateTime time;
    @Column(name = "event_id")
    private int eventId;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="branch_id", nullable = false)
    private Branch branch;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="department_id", nullable = false)
    private Department department;
    @Column(name = "target")
    private Long target;
    @Column(name = "old_value")
    private String oldValue;
    @Column(name = "new_value")
    private String newValue;

    public EventLog() {
    }

    public Long getId() {return id;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

    public int getEventId() {return eventId;}

    public void setEventId(int eventId) {this.eventId = eventId;}

    public LocalDateTime getTime() {return time;}

    public void setTime(LocalDateTime time) {this.time = time;}

    public Branch getBranch() {return branch;}

    public void setBranch(Branch branch) {this.branch = branch;}

    public Department getDepartment() {return department;}

    public void setDepartment(Department department) {this.department = department;}

    public Long getTarget() {return target;}

    public void setTarget(Long target) {this.target = target;}

    public String getOldValue() {return oldValue;}

    public void setOldValue(String oldValue) {this.oldValue = oldValue;}

    public String getNewValue() {return newValue;}

    public void setNewValue(String newValue) {this.newValue = newValue;}

}
