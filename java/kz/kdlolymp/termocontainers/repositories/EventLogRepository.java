package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EventLogRepository extends JpaRepository<EventLog, Long> {

    EventLog findEventLogById (Long logId);

    List<EventLog> findAll();


}
