package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="between_points")
public class BetweenPoint implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;
    @Column(name = "container_note_id")
    private Long containerNoteId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User passUser;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Department department;
    @Column(name = "pass_time")
    private LocalDateTime passTime;
    @Column(name="point_note")
    private String pointNote;
    public BetweenPoint() {
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Long getContainerNoteId() {return containerNoteId;}

    public void setContainerNoteId(Long containerNoteId) {this.containerNoteId = containerNoteId;}

    public User getPassUser() {return passUser;}

    public void setPassUser(User passUser) {this.passUser = passUser;}

    public Department getDepartment() {return department;}

    public void setDepartment(Department department) {this.department = department;}

    public LocalDateTime getPassTime() {return passTime;}

    public void setPassTime(LocalDateTime passTime) {this.passTime = passTime;}

    public String getPointNote() {return pointNote;}

    public void setPointNote(String pointNote) {this.pointNote = pointNote;}
}
