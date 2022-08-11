package kz.kdlolymp.termocontainers.entity;

import javax.persistence.*;

@Entity
@Table(name="container_values")
public class ContainerValue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "value_id")
    private Integer id;
    @Column(name = "value_name")
    private String valueName;

    public ContainerValue() {
    }

    public Integer getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getValueName() {return valueName;}

    public void setValueName(String valueName) {this.valueName = valueName;}
}
