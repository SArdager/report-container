package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.TimeStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TimeStandardRepository extends JpaRepository<TimeStandard, Integer> {

    TimeStandard findById(int timeStandardId);
    List<TimeStandard> findAllByFirstPointId(int firstPointId);

}
