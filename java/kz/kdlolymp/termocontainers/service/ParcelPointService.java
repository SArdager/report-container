package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Parcel;
import kz.kdlolymp.termocontainers.entity.ParcelPoint;
import kz.kdlolymp.termocontainers.repositories.ParcelPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParcelPointService {

    @Autowired
    private ParcelPointRepository parcelPointRepository;
    @PersistenceContext
    private EntityManager manager;


    public boolean savePoint(ParcelPoint point){
        parcelPointRepository.save(point);
        return true;
    }
    public List<ParcelPoint> findPointsFromParent(String parcelNumber){
        List<ParcelPoint> points = new ArrayList<>();
        try{
            points = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.parent = :paramParent", ParcelPoint.class)
                    .setParameter("paramParent", parcelNumber).getResultList();
        } catch (NoResultException ex){}

        return points;
    }

    public boolean removePoint(String parcelNumber){
        int rowsDeleted = manager.createQuery("DELETE FROM ParcelPoint pp WHERE pp.parent = :paramParent", ParcelPoint.class)
                .setParameter("paramParent", parcelNumber).executeUpdate();
        if(rowsDeleted>0){return true;}
        else{return false;}
    }

    public ParcelPoint findParcelPoint(String parcelNumber, int departmentId){
        List<ParcelPoint> points = parcelPointRepository.findAllByParcelNumber(parcelNumber);
        ParcelPoint point = new ParcelPoint();
        if(points!=null && points.size()>0) {
            try {
                point = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.parcelNumber = :paramParcel AND " +
                                "pp.department.id = :paramDep AND pp.sendTime IS NULL", ParcelPoint.class).setParameter("paramParcel", parcelNumber)
                                .setParameter("paramDep", departmentId).setMaxResults(1).getSingleResult();
            } catch (NoResultException ex) {
                point = null;
            }
        }
        return  point;
    }

    public ParcelPoint findOutParcelPoint(String parcelNumber){
        ParcelPoint point = new ParcelPoint();
        try {
            point = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.parcelNumber = :paramParcel AND " +
                            "pp.arriveTime IS NULL", ParcelPoint.class).setParameter("paramParcel", parcelNumber).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex){}
        return  point;
    }

    public int getSendParcelsNumberByDepartment(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int count = 0;
        try {
            List<ParcelPoint> points = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.department.id = :paramDepartment AND pp.payment > 0 AND " +
                            "pp.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY pp.sendTime ASC", ParcelPoint.class).setParameter("paramDepartment", departmentId)
                    .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setMaxResults(1000).getResultList();
            count = points.size();
        } catch (NoResultException ex){}
        return count;
    }

    public int getSendParcelsNumberByBranch(int branchId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int count = 0;
        try {
            List<ParcelPoint> points = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.department.branch.id = :paramBranch AND pp.payment > 0 AND " +
                  "pp.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY pp.sendTime ASC", ParcelPoint.class).setParameter("paramBranch", branchId)
                  .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setMaxResults(1000).getResultList();
            count = points.size();
        } catch (NoResultException ex){}
        return count;
    }

    public List<ParcelPoint> getSendParcelsByDepartment(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<ParcelPoint> points = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            query = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.department.id = :paramDepartment AND " +
                    "pp.payment > 0 AND pp.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY pp.sendTime ASC", ParcelPoint.class)
                    .setParameter("paramDepartment", departmentId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            points = query.getResultList();
        } catch (NoResultException ex){}
        return points;
    }

    public List<ParcelPoint> getSendParcelsByBranch(int branchId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<ParcelPoint> points = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            query = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.department.id = :paramBranch AND " +
                    "pp.payment > 0 AND pp.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY pp.sendTime ASC", ParcelPoint.class)
                    .setParameter("paramBranch", branchId).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            points = query.getResultList();
        } catch (NoResultException ex){}
        return points;
    }

    public int getAllPayingSendParcelsNumber(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query;
        int count = 0;
        try {
            query = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.payment > 0 AND pp.sendTime " +
                    "BETWEEN :paramStart AND :paramEnd ORDER BY pp.department.id ASC", ParcelPoint.class)
                    .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).setMaxResults(5000);
            count = query.getResultList().size();
        } catch (NoResultException ex){}
        return count;
    }

    public List<ParcelPoint> getPayingSendParcels(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        Query query;
        List<ParcelPoint> points = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        try {
            query = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.payment > 0 AND pp.sendTime " +
                            "BETWEEN :paramStart AND :paramEnd ORDER BY pp.department.branch.id ASC, pp.sendTime ASC",
                    ParcelPoint.class).setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            points = query.getResultList();
        } catch (NoResultException ex){}
        return points;
    }

    public List<ParcelPoint> getPointParcelsForExcel(int departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<ParcelPoint> points = new ArrayList<>();
        try {
            if(departmentId>1){
                points = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.payment > 0 AND pp.department.id = :paramDep AND " +
                                "pp.sendTime BETWEEN :paramStart AND :paramEnd ORDER BY pp.sendTime ASC",
                                ParcelPoint.class).setParameter("paramDep", departmentId).setParameter("paramStart", startDateTime)
                                .setParameter("paramEnd", endDateTime).getResultList();
            } else {
                points = manager.createQuery("SELECT pp FROM ParcelPoint pp WHERE pp.payment > 0 AND pp.sendTime BETWEEN " +
                              ":paramStart AND :paramEnd ORDER BY pp.department.branch.id ASC, pp.sendTime ASC", ParcelPoint.class)
                              .setParameter("paramStart", startDateTime).setParameter("paramEnd", endDateTime).getResultList();
            }
        } catch (NoResultException ex){}
        return points;
    }


}
