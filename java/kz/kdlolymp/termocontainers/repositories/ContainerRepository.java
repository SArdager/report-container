package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.Container;
import kz.kdlolymp.termocontainers.entity.ContainerValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ContainerRepository extends JpaRepository<Container, Integer> {
    Container findContainerById(int containerId);
    Container findContainerByContainerNumber(String containerNumber);
    List<Container> findAllByDepartmentId(int departmentId);
    List<Container> findAllByDepartmentIdOrderByContainerNumber(int departmentId);
    boolean existsByValue(ContainerValue value);
}
