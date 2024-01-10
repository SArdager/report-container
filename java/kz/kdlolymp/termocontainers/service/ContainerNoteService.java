package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Container;
import kz.kdlolymp.termocontainers.entity.ContainerNote;
import kz.kdlolymp.termocontainers.repositories.ContainerNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContainerNoteService {
    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private ContainerNoteRepository noteRepository;

    public ContainerNote findContainerNoteById(Long id){
        return noteRepository.findContainerNoteById(id);
    }
    public boolean saveNote(ContainerNote containerNote){
        ContainerNote savedNote = noteRepository.save(containerNote);
        if(savedNote!=null) {
            return true;
        } else{
            return  false;
        }
    }

    public boolean isContainerSend(Container container){
        int containerId = container.getId();
        List<ContainerNote> notes = new ArrayList<>();
        try {
            notes = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.isSend = true AND cn.container.id = :paramId",
                    ContainerNote.class).setParameter("paramId", containerId).getResultList();
        } catch (NoResultException ex){}
        if(notes!=null && notes.size()>0){
            return true;
        } else {
            return false;
        }
    }
    public ContainerNote findSentContainer(Container container){
        int departmentId = container.getDepartment().getId();
        int containerId = container.getId();
        ContainerNote note = new ContainerNote();
        try {
            note = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.outDepartment.id = :paramDepartment AND cn.container.id = :paramId " +
                    " AND cn.isSend = true", ContainerNote.class).setParameter("paramDepartment", departmentId).setParameter("paramId", containerId).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex){}
        return note;
    }
    public List<ContainerNote> findSentContainerToDepartment(int departmentId) {
        List<ContainerNote> notes = new ArrayList<>();
        try{
            notes = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.isSend = true AND cn.toDepartment.id = :paramDepartment ",
                    ContainerNote.class).setParameter("paramDepartment", departmentId).getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public ContainerNote findNoteByContainer(String containerNumber){
        ContainerNote note  = new ContainerNote();
        try{
            note  = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.containerNumber = :paramNumber " +
                    " AND cn.isSend = true", ContainerNote.class).setParameter("paramNumber", containerNumber).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex){}
        return note;
    }
    public ContainerNote findLastNoteByContainer(String containerNumber){
        ContainerNote note  = new ContainerNote();
        try{
            note  = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.containerNumber = :paramNumber" +
                    " ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramNumber", containerNumber)
                    .setMaxResults(1).getSingleResult();
        } catch (NoResultException ex){}
        return note;
    }

    public boolean checkNotesByDepartmentId (int departmentId){
        List<ContainerNote> notes = new ArrayList<>();
        try {
            notes =  manager.createQuery("SELECT cn FROM ContainerNote cn LEFT JOIN BetweenPoint bp ON cn.id = bp.containerNote.id " +
                "WHERE cn.outDepartment.id = :paramDepartment OR cn.toDepartment.id = :paramDepartment OR bp.department.id = :paramDepartment ORDER BY cn.sendTime DESC",
                ContainerNote.class).setParameter("paramDepartment", departmentId).getResultList();
        } catch (NoResultException ex){}
        if(notes!=null && notes.size()>0) {
            return true;
        }
        return false;
    }
    public boolean checkNotesByContainerId (int containerId){
        List<ContainerNote> notes = new ArrayList<>();
        try {
            notes = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.id = :parContainerId",
                    ContainerNote.class).setParameter("parContainerId", containerId).getResultList();
        } catch (NoResultException ex){}
        if(notes!=null && notes.size()>0) {
            return true;
        }
        return false;
    }

    public int getAllNotesNumberByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<ContainerNote> notes;
        int count = 0;
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT DISTINCT cn FROM ContainerNote cn LEFT JOIN BetweenPoint bp ON cn.id = bp.containerNote.id " +
                                "WHERE (cn.outDepartment.id = :paramDepartment AND cn.sendTime BETWEEN :paramStart AND :paramEnd) " +
                                "OR (cn.toDepartment.id = :paramDepartment AND cn.arriveTime BETWEEN :paramStart AND :paramEnd) " +
                                "OR (bp.department.id = :paramDepartment AND bp.passTime BETWEEN :paramStart AND :paramEnd) ORDER BY cn.sendTime DESC",
                                ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            notes = query.setMaxResults(1000).getResultList();
            count = notes.size();
        } catch (NoResultException ex){}
        return count;
    }

    public List<ContainerNote> getAllNotesOnRoute(int departmentId) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT DISTINCT cn FROM ContainerNote cn LEFT JOIN BetweenPoint bp ON cn.id = bp.containerNote.id " +
                                "WHERE (cn.outDepartment.id = :paramDepartment AND cn.isSend = true) " +
                                "OR (cn.toDepartment.id = :paramDepartment AND cn.isSend = true) " +
                                "OR (bp.department.id = :paramDepartment AND cn.isSend = true) ORDER BY cn.sendTime DESC",
                                ContainerNote.class).setParameter("paramDepartment", departmentId);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.isSend = true ORDER BY cn.sendTime DESC", ContainerNote.class);
            }
            notes = query.setMaxResults(500).getResultList();
        } catch (NoResultException ex){}
        return notes;
    }

    public List<ContainerNote> getAllNotesAtHome(int[] index) {
        List<ContainerNote> notes = new ArrayList<>();
        try {
            for(int i=0; i<index.length; i++){
                ContainerNote note = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.id = :paramContainer " +
                        "ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramContainer", index[i]).setMaxResults(1).getSingleResult();
                if(note.getArriveTime()!=null) {
                    notes.add(note);
                }
            }
        } catch (NoResultException ex){}
        return notes;
    }
    public int getAllPayNumberByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<ContainerNote> notes;
        int count = 0;
        try {
            Query query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE (cn.outDepartment.id = :paramDepartment AND cn.paidEnd = false AND cn.sendPay > 0 " +
                            "AND cn.sendTime BETWEEN :paramStart AND :paramEnd) OR (cn.toDepartment.id = :paramDepartment AND cn.paidEnd = true AND cn.sendPay > 0 " +
                            "AND cn.sendTime BETWEEN :paramStart AND :paramEnd) ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                            .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            notes = query.setMaxResults(1000).getResultList();
            count = notes.size();
        } catch (NoResultException ex){}
        return count;
    }

    public int getAllPayNumberByDatesAndBranchId(int branchId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<ContainerNote> notes;
        int count = 0;
        try {
            if(branchId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE (cn.sendPay > 0 AND cn.paidEnd = false AND cn.outDepartment.branch.id = :paramBranch " +
                         "AND cn.sendTime BETWEEN :paramStart AND :paramEnd) OR  (cn.sendPay > 0 AND cn.paidEnd = true AND cn.toDepartment.branch.id = :paramBranch " +
                         "AND cn.sendTime BETWEEN :paramStart AND :paramEnd) ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramBranch", branchId)
                        .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendTime BETWEEN :paramStart AND :paramEnd AND cn.sendPay > 0 ORDER BY cn.outDepartment.branch.id ASC, " +
                                "cn.sendTime DESC", ContainerNote.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            notes = query.setMaxResults(1000).getResultList();
            count = notes.size();
        } catch (NoResultException ex){}
        return count;
    }
    public int getAllDelayNumberByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Long delayLimit, String way) {
        Query query;
        List<ContainerNote> notes;
        int count = 0;
        try {
            if(departmentId>1) {
                if(way.equals("out")) {
                    query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit AND cn.outDepartment.id = :paramDepartment " +
                                    "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                    .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setParameter("paramLimit", delayLimit);
                } else{
                    query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit AND cn.toDepartment.id = :paramDepartment " +
                                    "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                    .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setParameter("paramLimit", delayLimit);
                }
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC",
                            ContainerNote.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setParameter("paramLimit", delayLimit);
            }
            notes = query.setMaxResults(1000).getResultList();
            count = notes.size();
        } catch (NoResultException ex){}
        return count;
    }

    public int getAllNumberByDatesAndContainerId(int containerId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<ContainerNote> notes;
        int count = 0;
        try {
            query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.id = :paramContainer AND " +
                            "cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramContainer", containerId)
                            .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            notes = query.setMaxResults(1000).getResultList();
            count = notes.size();
        } catch (NoResultException ex){}
        return count;
    }

    public List<ContainerNote> findNotesByContainer(String containerNumber, LocalDateTime endDateTime){
        List<ContainerNote> notes = new ArrayList<>();
        try{
            notes  = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.containerNumber = :paramNumber AND cn.arriveTime <= :paramDate" +
                            " ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramNumber", containerNumber).setParameter("paramDate", endDateTime)
                            .setMaxResults(50).getResultList();
        } catch (NoResultException ex){}
        return notes;
    }

    public List<ContainerNote> findNotesByContainerNumber(String containerNumber){
        List<ContainerNote> notes = new ArrayList<>();
        try{
            notes  = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.containerNumber = :paramNumber ORDER BY cn.sendTime DESC",
                            ContainerNote.class).setParameter("paramNumber", containerNumber).setMaxResults(50).getResultList();
        } catch (NoResultException ex){}
        return notes;
    }

    public List<ContainerNote> getNotesByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT DISTINCT cn FROM ContainerNote cn LEFT JOIN BetweenPoint bp ON cn.id = bp.containerNote.id " +
                                "WHERE (cn.outDepartment.id = :paramDepartment AND cn.sendTime BETWEEN :paramStart AND :paramEnd) " +
                                "OR (cn.toDepartment.id = :paramDepartment AND cn.sendTime BETWEEN :paramStart AND :paramEnd) " +
                                "OR (bp.department.id = :paramDepartment AND cn.sendTime BETWEEN :paramStart AND :paramEnd) " +
                                "ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }

    public List<ContainerNote> getNotesForExportExcel(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT DISTINCT cn FROM ContainerNote cn LEFT JOIN BetweenPoint bp ON cn.id = bp.containerNote.id " +
                                "WHERE (cn.outDepartment.id = :paramDepartment AND cn.sendTime BETWEEN :paramStart AND :paramEnd) " +
                                "OR (cn.toDepartment.id = :paramDepartment AND cn.sendTime BETWEEN :paramStart AND :paramEnd) " +
                                "OR (bp.department.id = :paramDepartment AND cn.sendTime BETWEEN :paramStart AND :paramEnd) " +
                                "ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            query.setMaxResults(5000);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getDelayForExportExcel(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Long delayLimit) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT DISTINCT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit AND cn.outDepartment.id = :paramDepartment " +
                                "AND cn.sendTime BETWEEN :paramStart AND :paramEnd OR cn.delayTime > :paramLimit AND cn.toDepartment.id = :paramDepartment AND cn.sendTime " +
                                "BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setParameter("paramLimit", delayLimit);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC",
                            ContainerNote.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setParameter("paramLimit", delayLimit);
            }
            notes = query.setMaxResults(5000).getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getPaymentForExportExcel(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE (cn.sendPay > 0 AND cn.paidEnd = false AND cn.outDepartment.id = :paramDepartment AND cn.sendTime " +
                                "BETWEEN :paramStart AND :paramEnd) OR (cn.sendPay > 0 AND cn.paidEnd = true AND cn.toDepartment.id = :paramDepartment AND cn.sendTime " +
                                "BETWEEN :paramStart AND :paramEnd) ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendTime BETWEEN :paramStart AND :paramEnd AND cn.sendPay > 0 ORDER BY " +
                                "cn.outDepartment.branch.id ASC, cn.sendTime DESC", ContainerNote.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            notes = query.setMaxResults(5000).getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getRouteForExportExcel(int containerId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        try {
            query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.id = :paramContainer AND " +
                            "cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramContainer", containerId)
                    .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            notes = query.setMaxResults(5000).getResultList();
        } catch (NoResultException ex){}
        return notes;
    }

    public List<ContainerNote> getPaymentNotesByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            Query query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE (cn.outDepartment.id = :paramDepartment AND cn.sendPay > 0 AND cn.paidEnd = false " +
                            "AND cn.sendTime BETWEEN :paramStart AND :paramEnd) OR (cn.toDepartment.id = :paramDepartment AND cn.sendPay > 0 AND cn.paidEnd = true " +
                            "AND cn.sendTime BETWEEN :paramStart AND :paramEnd) ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                            .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getPaymentNotesByDatesAndBranchId(int branchId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        try {
            if(branchId > 1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE (cn.outDepartment.branch.id = :paramBranch AND cn.sendPay > 0 AND cn.paidEnd = false " +
                                "AND cn.sendTime BETWEEN :paramStart AND :paramEnd) OR (cn.toDepartment.branch.id = :paramBranch AND cn.sendPay > 0 AND cn.paidEnd = true " +
                                "AND cn.sendTime BETWEEN :paramStart AND :paramEnd) ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramBranch", branchId)
                        .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendPay > 0 AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.outDepartment.branch.id ASC, cn.sendTime DESC",
                        ContainerNote.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getDelayNotesByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable, Long delayLimit, String way) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        try {
            if(departmentId>1) {
                if(way.equals("out")) {
                    query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit AND cn.outDepartment.id = :paramDepartment " +
                                    "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                    .setParameter("paramLimit", delayLimit).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                } else {
                    query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit AND cn.toDepartment.id = :paramDepartment " +
                                    "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                    .setParameter("paramLimit", delayLimit).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
                }
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC",
                                ContainerNote.class).setParameter("paramLimit", delayLimit).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getNotesByDatesAndContainerId(int containerId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        try {
            query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.id = :paramContainer AND " +
                            "cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.sendTime DESC", ContainerNote.class).
                            setParameter("paramContainer", containerId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }

    public boolean checkUsedContainer(int containerId, LocalDateTime controlDate, LocalDateTime currentDate){
        List<ContainerNote> notes = new ArrayList<>();
        try {
            notes = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.id = :paramId AND cn.sendTime BETWEEN :paramStart AND :paramEnd",
                    ContainerNote.class).setParameter("paramId", containerId).setParameter("paramStart", controlDate).setParameter("paramEnd", currentDate).getResultList();
        } catch (NoResultException ex){}
        if(notes!=null && notes.size()>0){
            return true;
        } else {
            return false;
        }
    }

}
