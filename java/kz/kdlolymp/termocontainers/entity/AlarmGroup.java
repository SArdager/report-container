package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "alarm_groups")
public class AlarmGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Integer id;
    @Column(name = "alarm_group_name")
    private String alarmGroupName;

    @ManyToMany(targetEntity = User.class, cascade = CascadeType.ALL,
            fetch = FetchType.LAZY )
    @JoinTable(
            name="alarm_users",
            joinColumns = @JoinColumn(name = "alarm_group_id"))
    private List<User> users;

    public AlarmGroup() {
    }

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    public String getAlarmGroupName() {return alarmGroupName;}

    public void setAlarmGroupName(String alarmGroupName) {this.alarmGroupName = alarmGroupName;}

    public List<User> getUsers() {return users;}

    public void setUsers(List<User> users) {this.users = users;}

    public void addUser(User user){
        users.add(user);
        user.getAlarmGroups().add(this);
    }
    public void removeUser(User user){
        users.remove(user);
        user.getAlarmGroups().remove(this);
    }
}
