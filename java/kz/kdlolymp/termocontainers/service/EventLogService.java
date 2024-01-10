package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.repositories.EventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventLogService {

    @Autowired
    private EventLogRepository eventLogRepository;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private EntityManager manager;

    public EventLog findEventLogById(Long id){return eventLogRepository.findEventLogById(id);}

    public List<EventLog> isEventLogByEventId(int eventId){
        List<EventLog> logs = new ArrayList<>();
        try{
            logs = manager.createQuery("SELECT l FROM EventLog l WHERE l.eventId = :param",
                    EventLog.class).setParameter("param", eventId).setMaxResults(500).getResultList();
        } catch (NoResultException ex){}

        return logs;
    }

    public void saveEvent(User userEditor, int departmentId, Long index, String newValue, String oldValue, int eventId){
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Department department = departmentService.findDepartmentById(departmentId);
        Branch branch = department.getBranch();
        EventLog log = new EventLog();
        log.setDepartment(department);
        log.setBranch(branch);
        log.setUser(userEditor);
        log.setTarget(index);
        log.setNewValue(newValue);
        log.setOldValue(oldValue);
        log.setTime(currentDateTime);
        log.setEventId(eventId);
        eventLogRepository.save(log);
    }

    public int getAllLogsNumberByDates(int eventId, int branchId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<EventLog> logs;
        int count = 0;
        try {
            if(eventId==1) {
                if (branchId > 1) {
                    query = manager.createQuery("SELECT el FROM EventLog el WHERE el.eventId = :paramEvent AND el.branch.id = :paramBranch AND el.time BETWEEN :paramStart " +
                                "AND :paramEnd ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramBranch", branchId)
                            .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                } else {
                    query = manager.createQuery("SELECT el FROM EventLog el WHERE el.eventId = :paramEvent AND el.time BETWEEN :paramStart AND :paramEnd ORDER BY el.id DESC",
                                EventLog.class).setParameter("paramEvent", eventId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                }
            } else {
                if (branchId > 1) {
                    query = manager.createQuery("SELECT el FROM EventLog el WHERE el.eventId = :paramEvent AND el.branch.id = :paramBranch AND el.time BETWEEN :paramStart " +
                                "AND :paramEnd ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramBranch", branchId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                } else {
                    query = manager.createQuery("SELECT el FROM EventLog el WHERE el.eventId = :paramEvent AND el.time BETWEEN :paramStart AND :paramEnd ORDER BY el.id DESC",
                                EventLog.class).setParameter("paramEvent", eventId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                }
            }
            logs = query.setMaxResults(1000).getResultList();
            count = logs.size();
        } catch (NoResultException ex){}
        return count;
    }

    public List<EventLog> getLogsByDates(int eventId, int branchId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<EventLog> logs = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            if(eventId==1) {
                if (branchId > 1) {
                    query = manager.createQuery("SELECT el FROM EventLog el LEFT JOIN Department d ON el.target = d.id WHERE el.eventId = :paramEvent AND el.branch.id = :paramBranch AND el.time " +
                                "BETWEEN :paramStart AND :paramEnd ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramBranch", branchId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                } else {
                    query = manager.createQuery("SELECT el FROM EventLog el LEFT JOIN Department d ON el.target = d.id WHERE el.eventId = :paramEvent AND el.time BETWEEN :paramStart AND :paramEnd " +
                                "ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                }
            } else {
                if (branchId > 1) {
                    query = manager.createQuery("SELECT el FROM EventLog el LEFT JOIN User u ON el.target = u.id WHERE el.eventId = :paramEvent AND el.branch.id = :paramBranch AND el.time " +
                                "BETWEEN :paramStart AND :paramEnd ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramBranch", branchId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                } else {
                    query = manager.createQuery("SELECT el FROM EventLog el LEFT JOIN User u ON el.target = u.id WHERE el.eventId = :paramEvent AND el.time BETWEEN :paramStart AND :paramEnd " +
                                "ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                }
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            logs = query.getResultList();
        } catch (NoResultException ex){}
        return logs;
    }

    public List<EventLog> getLogsForExportExcel(int eventId, int branchId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<EventLog> logs = new ArrayList<>();
        try {
            if(eventId==1) {
                if (branchId > 1) {
                    query = manager.createQuery("SELECT el FROM EventLog el LEFT JOIN Department d ON el.target = d.id WHERE el.eventId = :paramEvent AND el.branch.id = :paramBranch AND " +
                                "el.time BETWEEN :paramStart AND :paramEnd ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramBranch", branchId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                } else {
                    query = manager.createQuery("SELECT el FROM EventLog el LEFT JOIN Department d ON el.target = d.id WHERE el.eventId = :paramEvent AND el.time BETWEEN :paramStart " +
                                "AND :paramEnd ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                }
            } else {
                if (branchId > 1) {
                    query = manager.createQuery("SELECT el FROM EventLog el LEFT JOIN User u ON el.target = u.id WHERE el.eventId = :paramEvent AND el.branch.id = :paramBranch AND " +
                                "el.time BETWEEN :paramStart AND :paramEnd ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramBranch", branchId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                } else {
                    query = manager.createQuery("SELECT el FROM EventLog el LEFT JOIN User u ON el.target = u.id WHERE el.eventId = :paramEvent AND el.time BETWEEN :paramStart " +
                                "AND :paramEnd ORDER BY el.id DESC", EventLog.class).setParameter("paramEvent", eventId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                }
            }
            logs = query.setMaxResults(5000).getResultList();
        } catch (NoResultException ex){}
        return logs;
    }
}
