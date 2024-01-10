package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;

@Entity
@Table(name="time_standards")
public class TimeStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_standard_id")
    private Integer id;
    @Column(name = "time_standard")
    private int timeStandard;
    @Column(name = "first_point_id")
    private int firstPointId;
    @Column(name = "second_point_id")
    private int secondPointId;

    public TimeStandard() {
    }

    public Integer getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getTimeStandard() { return timeStandard; }

    public void setTimeStandard(int timeStandard) { this.timeStandard = timeStandard; }

    public int getFirstPointId() { return firstPointId; }

    public void setFirstPointId(int firstPointId) { this.firstPointId = firstPointId; }

    public int getSecondPointId() { return secondPointId; }

    public void setSecondPointId(int secondPointId) { this.secondPointId = secondPointId; }

    @Override
    public String toString() {
        return "TimeStandard{" +
                "timeStandardId=" + id +
                ", timeStandard=" + timeStandard +
                ", firstPointId=" + firstPointId +
                ", secondPointId=" + secondPointId +
                '}';
    }
}
