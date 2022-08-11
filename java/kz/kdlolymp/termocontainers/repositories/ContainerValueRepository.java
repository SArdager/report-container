package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.ContainerValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface ContainerValueRepository extends JpaRepository<ContainerValue, Integer> {

    ContainerValue findContainerValueById(int containerValueId);
    ContainerValue findContainerValueByValueName(String valueName);
}
