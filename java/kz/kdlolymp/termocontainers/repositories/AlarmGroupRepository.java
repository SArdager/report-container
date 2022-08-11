package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.AlarmGroup;
import kz.kdlolymp.termocontainers.entity.Branch;
import kz.kdlolymp.termocontainers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlarmGroupRepository extends JpaRepository<AlarmGroup, Integer> {
    AlarmGroup findAlarmGroupById(int id);
    AlarmGroup findAlarmGroupByAlarmGroupName(String alarmGroupName);

    List<AlarmGroup> findAll();

}
