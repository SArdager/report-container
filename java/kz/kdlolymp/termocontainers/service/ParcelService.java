package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.repositories.ParcelRepository;
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
public class ParcelService {

    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private DepartmentService departmentService;
    @PersistenceContext
    private EntityManager manager;

    public Parcel findParcelByNumber(String parcelNumber){
        return parcelRepository.findParcelByParcelNumber(parcelNumber);
    }
    public boolean saveParcel(Parcel parcel){
        parcelRepository.save(parcel);
        return true;
    }
    public String addNewParcel(String parcelType, int destinationId, int outDepartmentId, User createUser, String dimensions,
                               String note, boolean information, LocalDateTime createDate, User sendUser, boolean isWaited) {
        Department outDepartment = departmentService.findDepartmentById(outDepartmentId);
        Department destination = departmentService.findDepartmentById(destinationId);
        List<ParcelPoint> parcelPoints = new ArrayList<>();
        Parcel parcel = new Parcel(createDate, createUser, outDepartment, destination, sendUser, parcelPoints);
        Parcel createdParcel = parcelRepository.save(parcel);
        String parcelIdString = createdParcel.getId() + "";
        while (parcelIdString.length()<7){
            parcelIdString = 0 + parcelIdString;
        }
        String parcelNumber = parcelType + parcelIdString;
        createdParcel.setParcelNumber(parcelNumber);
        createdParcel.setNote(note);
        createdParcel.setDimensions(dimensions);
        createdParcel.setInformation(information);
        createdParcel.setCurrentDepartmentId(outDepartmentId);
        createdParcel.setWaited(isWaited);
        createdParcel.setCostsPart(0);
        Parcel newParcel = parcelRepository.save(createdParcel);
        return newParcel.getParcelNumber();
    }

    public List<Parcel> findAllByParentNumber(String parentNumber){
        return parcelRepository.findAllByParentNumber(parentNumber);
    }

    public int getAllParcelsNumberByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<Parcel> parcels;
        int count = 0;
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT DISTINCT p FROM Parcel p WHERE p.outDepartment.id = :paramDepartment " +
                                "AND p.sendDate BETWEEN :paramStart AND :paramEnd OR p.destination.id = :paramDepartment " +
                                "AND p.sendDate BETWEEN :paramStart AND :paramEnd ORDER BY p.sendDate DESC",
                                Parcel.class).setParameter("paramDepartment", departmentId).setParameter("paramStart", startDateTime)
                                .setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT DISTINCT p FROM Parcel p WHERE p.sendDate BETWEEN :paramStart AND :paramEnd " +
                                        "ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            parcels = query.setMaxResults(1000).getResultList();
            count = parcels.size();
        } catch (NoResultException ex){}
        return count;
    }

    public int getAllOutParcelsNumberByDepartment(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int count = 0;
        try {
            List<Parcel> parcels = manager.createQuery("SELECT p FROM Parcel p WHERE p.outDepartment.id = :paramDepartment AND " +
                              "p.sendDate BETWEEN :paramStart AND :paramEnd ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramDepartment", departmentId)
                              .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setMaxResults(1000).getResultList();
            count = parcels.size();
        } catch (NoResultException ex){}
        return count;
    }

    public int getAllToParcelsNumberByDepartment(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int count = 0;
        try {
            List<Parcel> parcels = manager.createQuery("SELECT p FROM Parcel p WHERE p.destination.id = :paramDepartment AND " +
                              "p.sendDate BETWEEN :paramStart AND :paramEnd ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramDepartment", departmentId)
                              .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setMaxResults(1000).getResultList();
            count = parcels.size();
        } catch (NoResultException ex){}
        return count;
    }

    public List<Parcel> getParcelsByDatesAndDepartmentId(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<Parcel> parcels = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT DISTINCT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId WHERE " +
                                "p.outDepartment.id = :paramDepartment AND p.sendDate BETWEEN :paramStart AND :paramEnd OR p.destination.id = :paramDepartment " +
                                "AND p.sendDate BETWEEN :paramStart AND :paramEnd ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT DISTINCT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId WHERE p.sendDate BETWEEN :paramStart " +
                                "AND :paramEnd ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            parcels = query.getResultList();
        } catch (NoResultException ex){}
        return parcels;
    }

    public List<Parcel> getParcelsForExportExcel(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<Parcel> parcels = new ArrayList<>();

        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT DISTINCT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId WHERE " +
                                "p.outDepartment.id = :paramDepartment AND p.sendDate BETWEEN :paramStart AND :paramEnd OR p.destination.id = :paramDepartment " +
                                "AND p.sendDate BETWEEN :paramStart AND :paramEnd ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramDepartment", departmentId)
                                .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT DISTINCT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId WHERE p.sendDate BETWEEN :paramStart " +
                                "AND :paramEnd ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            query.setMaxResults(2000);
            parcels = query.getResultList();
        } catch (NoResultException ex){}
        return parcels;
    }

    public List<Parcel> getSendParcelsForExcel(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        List<Parcel> parcels = new ArrayList<>();
        try {
            if(departmentId>1) {
            query = manager.createQuery("SELECT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId WHERE pp.department.id = :paramDepartment " +
                            "AND pp.payment > 0 AND pp.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY p.sendDate DESC", Parcel.class)
                            .setParameter("paramDepartment", departmentId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            } else {
                query = manager.createQuery("SELECT p FROM Parcel p WHERE p.payment > 0 AND " +
                            "p.sendDate BETWEEN :paramStart AND :paramEnd ORDER BY p.outDepartment.branch.id ASC, p.sendDate DESC", Parcel.class)
                            .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            }
            parcels = query.getResultList();
        } catch (NoResultException ex){}
        return parcels;
    }

    public List<Parcel> getOutParcelsByDepartment(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<Parcel> parcels = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            query = manager.createQuery("SELECT DISTINCT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId WHERE " +
                            "p.outDepartment.id = :paramDepartment AND p.sendDate BETWEEN :paramStart AND :paramEnd ORDER BY p.sendDate DESC", Parcel.class)
                            .setParameter("paramDepartment", departmentId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            parcels = query.getResultList();
        } catch (NoResultException ex){}
        return parcels;
    }

    public List<Parcel> getToParcelsByDepartment(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<Parcel> parcels = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            query = manager.createQuery("SELECT DISTINCT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId WHERE " +
                            "p.destination.id = :paramDepartment AND p.sendDate BETWEEN :paramStart AND :paramEnd ORDER BY p.sendDate DESC", Parcel.class)
                            .setParameter("paramDepartment", departmentId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            parcels = query.getResultList();
        } catch (NoResultException ex){}
        return parcels;
    }

    public List<Parcel> getAllParcelsOnRoute(int departmentId) {
        Query query;
        List<Parcel> parcels = new ArrayList<>();

        try {
            if(departmentId>1) {
                query = manager.createQuery("SELECT DISTINCT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId " +
                        "WHERE p.outDepartment.id = :paramDepartment AND p.isDelivered = false OR p.destination.id = :paramDepartment AND " +
                        "p.isDelivered = false ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramDepartment", departmentId);
            } else {
                query = manager.createQuery("SELECT DISTINCT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId " +
                                "WHERE p.isDelivered = false ORDER BY p.sendDate DESC",Parcel.class);
            }
            query.setMaxResults(100);
            parcels = query.getResultList();
        } catch (NoResultException ex){}
        return parcels;
    }

    public Parcel searchParcelByNumber(String parcelNumber){
        Parcel parcel = new Parcel();
        try {
            parcel = manager.createQuery("SELECT p FROM Parcel p LEFT JOIN ParcelPoint pp ON p.id = pp.parcelId WHERE " +
                            "p.parcelNumber = :paramNumber", Parcel.class).setParameter("paramNumber", parcelNumber).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex){}
        return parcel;
    }

    public List<Parcel> getWaitedParcelsByDepartmentId(int departmentId) {
        List<Parcel> parcels = new ArrayList<>();

        try {
            if(departmentId>1) {
                parcels = manager.createQuery("SELECT p FROM Parcel p WHERE p.currentDepartmentId = :paramDepartment AND p.isWaited = true " +
                        "ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramDepartment", departmentId).setMaxResults(100).getResultList();
            }
        } catch (NoResultException ex){}
        return parcels;
    }

    public List<Parcel> getMovedParcelsByDepartmentId(int departmentId) {
        List<Parcel> parcels = new ArrayList<>();

        try {
            if(departmentId>1) {
                parcels = manager.createQuery("SELECT p FROM Parcel p WHERE p.currentDepartmentId = :paramDepartment AND p.isWaited = false AND p.isDelivered = " +
                        "false ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramDepartment", departmentId).setMaxResults(100).getResultList();
            }
        } catch (NoResultException ex){}
        return parcels;
    }

    public List<Parcel> getWaitedParcelsByParent(String parentNumber) {
        List<Parcel> parcels = new ArrayList<>();

        try {
            parcels = manager.createQuery("SELECT p FROM Parcel p WHERE p.parentNumber = :paramParent AND p.isWaited = true " +
                    "ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramParent", parentNumber).setMaxResults(20).getResultList();
        } catch (NoResultException ex){}
        return parcels;
    }

    public List<Parcel> getMovedParcelsByParent(String parentNumber) {
        List<Parcel> parcels = new ArrayList<>();

        try {
            parcels = manager.createQuery("SELECT p FROM Parcel p WHERE p.parentNumber = :paramParent AND p.isWaited = false AND p.isDelivered = " +
                    "false ORDER BY p.sendDate DESC", Parcel.class).setParameter("paramParent", parentNumber).setMaxResults(20).getResultList();
        } catch (NoResultException ex){}
        return parcels;
    }

    public Parcel getWaitedParcelsByNumberAndDepartment(String parentNumber, int departmentId) {
        Parcel parcel = new Parcel();

        try {
            parcel = manager.createQuery("SELECT p FROM Parcel p WHERE p.parcelNumber = :paramNumber AND p.isWaited = true " +
                    "AND p.currentDepartmentId = :paramDep", Parcel.class).setParameter("paramNumber", parentNumber)
                    .setParameter("paramDep", departmentId).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex){}
        return parcel;
    }


}
