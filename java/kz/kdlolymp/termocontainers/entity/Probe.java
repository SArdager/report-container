package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;

@Entity
@Table(name="probes")
public class Probe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "probe_id")
    private Integer id;
    @Column(name = "probe_name")
    private String probeName;

    public Probe() {
    }

    public Integer getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getProbeName() { return probeName; }

    public void setProbeName(String probeName) { this.probeName = probeName; }

    @Override
    public String toString() {
        return "Probe{" +
                "probeId=" + id +
                ", probeName='" + probeName + '\'' +
                '}';
    }
}
