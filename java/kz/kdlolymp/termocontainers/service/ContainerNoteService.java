package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Container;
import kz.kdlolymp.termocontainers.entity.ContainerNote;
import kz.kdlolymp.termocontainers.repositories.ContainerNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

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
        noteRepository.save(containerNote);
        return true;
    }
    public boolean isContainerSend(Container container){
        int departmentId = container.getDepartment().getId();
        int containerId = container.getId();
        ContainerNote note = new ContainerNote();
        try {
            note = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.outDepartment.id = :paramDepartment AND cn.container.id = :paramId ORDER BY cn.sendTime DESC",
                    ContainerNote.class).setParameter("paramDepartment", departmentId).setParameter("paramId", containerId).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex){}
        if(note!=null){
            return note.isSend();
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
                    " AND cn.isSend = true", ContainerNote.class).setParameter("paramDepartment", departmentId).setParameter("paramId", containerId).getSingleResult();
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
                    " AND cn.isSend = true", ContainerNote.class).setParameter("paramNumber", containerNumber).getSingleResult();
        } catch (NoResultException ex){}
        return note;
    }
    public ContainerNote findNoteByContainerAndToDepartment(String containerNumber, int departmentId){
        ContainerNote note  = new ContainerNote();
        try{
            note  = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.toDepartment.id = :paramDepartment AND cn.container.containerNumber = :paramNumber " +
                    " AND cn.isSend = true", ContainerNote.class).setParameter("paramDepartment", departmentId).setParameter("paramNumber", containerNumber).getSingleResult();
        } catch (NoResultException ex){}
        return note;
    }
    public ContainerNote findLastNoteByContainer(String containerNumber){
        ContainerNote note  = new ContainerNote();
        try{
            note  = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.containerNumber = :paramNumber" +
                    " ORDER BY cn.arriveTime DESC", ContainerNote.class).setParameter("paramNumber", containerNumber)
                    .setMaxResults(1).getSingleResult();
        } catch (NoResultException ex){}
        return note;
    }

    public List<ContainerNote> findAllByDepartmentId (int departmentId, Pageable pageable){
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn LEFT JOIN BetweenPoint bp ON cn.id = bp.containerNoteId " +
                                "WHERE cn.outDepartment.id = :paramDepartment OR cn.toDepartment.id = :paramDepartment OR bp.department.id = :paramDepartment ORDER BY cn.id DESC",
                                ContainerNote.class).setParameter("paramDepartment", departmentId);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn ORDER BY cn.id DESC", ContainerNote.class);
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
            if(notes.size()>0) {
                for (int i = 1; i < notes.size(); i++) {
                    if (notes.get(i).getId() == notes.get(i - 1).getId()) {
                        notes.remove(i);
                        i--;
                    }
                }
            }
        } catch (NoResultException ex){}

        return notes;
    }
    public List<ContainerNote> findPaymentNotesByDepartmentId (int departmentId, Pageable pageable){
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.outDepartment.id = :paramDepartment AND " +
                                "cn.sendPay > 0 ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramDepartment", departmentId);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendPay > 0 ORDER BY cn.id DESC", ContainerNote.class);
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getDelayNotesByDepartmentId (int departmentId, Pageable pageable, Long delayLimit){
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.outDepartment.id = :paramDepartment AND cn.delayTime > :paramLimit " +
                                "ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramDepartment", departmentId).setParameter("paramLimit", delayLimit);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramLimit", delayLimit);
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getNotesByContainerId (int containerId, Pageable pageable){
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.id = :paramContainer " +
                            "ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramContainer", containerId);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public long getAllNotesNumberByDepartmentId (int departmentId){
        Query query;
        long count = 0L;
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn LEFT JOIN BetweenPoint bp ON cn.id = bp.containerNoteId " +
                                "WHERE cn.outDepartment.id = :paramDepartment OR cn.toDepartment.id = :paramDepartment OR bp.department.id = :paramDepartment ORDER BY cn.id DESC",
                        ContainerNote.class).setParameter("paramDepartment", departmentId);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn ORDER BY cn.id DESC", ContainerNote.class);
            }
            List<ContainerNote> notes = query.setMaxResults(100).getResultList();
            if(notes.size()>0) {
                for (int i = 1; i < notes.size(); i++) {
                    if (notes.get(i).getId() == notes.get(i - 1).getId()) {
                        notes.remove(i);
                        i--;
                    }
                }
                count = (long) notes.size();
            }
        } catch (NoResultException ex){}
        return count;
    }
    public long getAllPayNumberByDepartmentId (int departmentId){
        Query query;
        long count = 0L;
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn " +
                                "WHERE cn.outDepartment.id = :paramDepartment AND cn.sendPay > 0 ORDER BY cn.id DESC",
                        ContainerNote.class).setParameter("paramDepartment", departmentId);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendPay > 0 ORDER BY cn.id DESC", ContainerNote.class);
            }
            List<ContainerNote> notes = query.setMaxResults(100).getResultList();
            count = (long) notes.size();
        } catch (NoResultException ex){}
        return count;
    }
    public long getAllDelayNumberByDepartmentId (int departmentId, Long delayLimit){
        Query query;
        long count = 0L;
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn " +
                                "WHERE cn.outDepartment.id = :paramDepartment AND cn.delayTime > :paramLimit ORDER BY cn.id DESC",
                        ContainerNote.class).setParameter("paramDepartment", departmentId).setParameter("paramLimit", delayLimit);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit ORDER BY cn.id DESC",
                        ContainerNote.class).setParameter("paramLimit", delayLimit);
            }
            List<ContainerNote> notes = query.setMaxResults(100).getResultList();
            count = (long) notes.size();
        } catch (NoResultException ex){}
        return count;
    }
    public long getAllNumberByContainerId (int containerId){
        Query query;
        long count = 0L;
        try {
            query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.id = :paramContainer " +
                            "ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramContainer", containerId);
            List<ContainerNote> notes = query.setMaxResults(100).getResultList();
            count = (long) notes.size();
        } catch (NoResultException ex){}
        return count;
    }

    public long getAllNotesNumberByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<ContainerNote> notes;
        long count = 0;
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn LEFT JOIN BetweenPoint bp ON cn.id = bp.containerNoteId " +
                                "WHERE cn.outDepartment.id = :paramDepartment OR cn.toDepartment.id = :paramDepartment OR bp.department.id = :paramDepartment " +
                                "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            notes = query.setMaxResults(100).getResultList();
            if(notes.size()>0) {
                for (int i = 1; i < notes.size(); i++) {
                    if (notes.get(i).getId() == notes.get(i - 1).getId()) {
                        notes.remove(i);
                        i--;
                    }
                }
                count = (long) notes.size();
            }
        } catch (NoResultException ex){}
        return count;
    }
    public long getAllPayNumberByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<ContainerNote> notes;
        long count = 0;
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.outDepartment.id = :paramDepartment AND cn.sendPay > 0 " +
                                "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendTime BETWEEN :paramStart AND :paramEnd AND cn.sendPay > 0 ORDER BY cn.id DESC",
                                ContainerNote.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            notes = query.setMaxResults(100).getResultList();
            count = (long) notes.size();
        } catch (NoResultException ex){}
        return count;
    }
    public long getAllDelayNumberByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Long delayLimit) {
        Query query;
        List<ContainerNote> notes;
        long count = 0;
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.outDepartment.id = :paramDepartment AND cn.delayTime > :paramLimit " +
                                "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setParameter("paramLimit", delayLimit);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendTime BETWEEN :paramStart AND :paramEnd AND cn.delayTime > :paramLimit ORDER BY cn.id DESC",
                                ContainerNote.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setParameter("paramLimit", delayLimit);
            }
            notes = query.setMaxResults(100).getResultList();
            count = (long) notes.size();
        } catch (NoResultException ex){}
        return count;
    }
    public long getAllNumberByDatesAndContainerId(int containerId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<ContainerNote> notes;
        long count = 0;
        try {
            query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.container.id = :paramContainer AND " +
                            "cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramContainer", containerId)
                            .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            notes = query.setMaxResults(100).getResultList();
            count = (long) notes.size();
        } catch (NoResultException ex){}
        return count;
    }
    public List<ContainerNote> getNotesByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn LEFT JOIN BetweenPoint bp ON cn.id = bp.containerNoteId " +
                                "WHERE cn.outDepartment.id = :paramDepartment OR cn.toDepartment.id = :paramDepartment OR bp.department.id = :paramDepartment " +
                                "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
            if(notes.size()>0) {
                for (int i = 1; i < notes.size(); i++) {
                    if (notes.get(i).getId() == notes.get(i - 1).getId()) {
                        notes.remove(i);
                        i--;
                    }
                }
            }
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getPaymentNotesByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.outDepartment.id = :paramDepartment AND cn.sendPay > 0 " +
                                "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.sendPay > 0 AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC",
                                ContainerNote.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }
    public List<ContainerNote> getDelayNotesByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable, Long delayLimit) {
        Query query;
        List<ContainerNote> notes = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.outDepartment.id = :paramDepartment AND cn.delayTime > :paramLimit " +
                                "AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramLimit", delayLimit).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT cn FROM ContainerNote cn WHERE cn.delayTime > :paramLimit AND cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC",
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
                            "cn.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY cn.id DESC", ContainerNote.class).
                            setParameter("paramContainer", containerId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            notes = query.getResultList();
        } catch (NoResultException ex){}
        return notes;
    }



}
