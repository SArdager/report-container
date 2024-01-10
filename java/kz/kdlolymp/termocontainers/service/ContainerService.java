package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.*;
import kz.kdlolymp.termocontainers.repositories.ContainerRepository;
import kz.kdlolymp.termocontainers.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContainerService {

    @PersistenceContext
    private EntityManager manager;
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ContainerNoteService containerNoteService;

    public boolean saveContainer(Container container){
        containerRepository.save(container);
        return true;
    }
    public Container findContainerById(int id){
        return containerRepository.findContainerById(id);
    }
    public Container findByContainerNumber(String containerNumber){
        return containerRepository.findContainerByContainerNumber(containerNumber);
    }

    public boolean addNewContainer(Container newContainer){
        Container container = findByContainerNumber(newContainer.getContainerNumber());
        if(container!=null){
            return false;
        } else{
            containerRepository.save(newContainer);
            return true;
        }
    }
    public boolean checkContainerForValue(ContainerValue value){
        return containerRepository.existsByValue(value);
    }

    public List<Container> findAll() {
        return containerRepository.findAll();
    }

    public List<Container> findAllOrdered() {
        List<Container> containers = new ArrayList<>();
        try{
            containers = manager.createQuery("SELECT c FROM Container c JOIN Department d ON c.department.id=d.id ORDER BY c.department.branch ASC, c.containerNumber ASC",
                    Container.class).getResultList();
        } catch (NoResultException ex){}
        return containers;
    }

    public void deleteContainer(int id){
        Container container = containerRepository.findContainerById(id);
        containerRepository.delete(container);
    }

    public List<Container> findAllByBranchId(int branchId){
        List<Container> containers = new ArrayList<>();
        List<Department> departments = departmentService.findAllByBranchId(branchId);
        if(departments!=null && departments.size()>0) {
            for (Department department : departments) {
                List<Container> containerList = containerRepository.findAllByDepartmentIdOrderByContainerNumber(department.getId());
                for (int i = 0; i < containerList.size(); i++) {
                    containers.add(containerList.get(i));
                }
            }
        }
        return containers;
    }

    public List<Container> findAllNoUsedByBranchId(int branchId, LocalDateTime controlDate, LocalDateTime currentDate){
        List<Container> containers = new ArrayList<>();
        List<Department> departments = departmentService.findAllByBranchId(branchId);
        if(departments!=null && departments.size()>0) {
            for (Department department : departments) {
                List<Container> containerList = containerRepository.findAllByDepartmentIdOrderByContainerNumber(department.getId());
                if(containerList!=null && containerList.size()>0) {
                    for (int i = 0; i < containerList.size(); i++) {
                        Container checkedContainer = containerList.get(i);
                        boolean usedContainer = containerNoteService.checkUsedContainer(checkedContainer.getId(), controlDate, currentDate);
                        if (!usedContainer) {
                            containers.add(checkedContainer);
                        }
                    }
                }
            }
        }
        return containers;
    }
    public List<Container> findAllNoUsedOrdered(LocalDateTime controlDate, LocalDateTime currentDate) {
        List<Container> containers = new ArrayList<>();
        List<Container> containerFromDb = new ArrayList<>();
        try{
            containerFromDb = manager.createQuery("SELECT c FROM Container c JOIN Department d ON c.department.id=d.id ORDER BY c.department.branch ASC, c.containerNumber ASC",
                    Container.class).getResultList();
        } catch (NoResultException ex){}
        for (int i = 0; i < containerFromDb.size(); i++) {
            Container checkedContainer = containerFromDb.get(i);
            boolean usedContainer = containerNoteService.checkUsedContainer(checkedContainer.getId(), controlDate, currentDate);
            if(!usedContainer) {
                containers.add(checkedContainer);
            }
        }
        return containers;
    }

    public List<Container> findAllByPartContainerNumber(String partNumber){
        List<Container> containers = new ArrayList<>();
        if(partNumber.length()>3){
            try{
                containers = manager.createQuery("SELECT c FROM Container c WHERE c.containerNumber LIKE CONCAT('%', :paramNumber, '%') " +
                        "ORDER BY c.id ASC", Container.class).setParameter("paramNumber", partNumber).getResultList();
            } catch (NoResultException ex){}
        } else {
            containers = findAll();
        }
        return containers;
    }

    public List<Container> findAllByDepartmentId(int departmentId){
        return containerRepository.findAllByDepartmentId(departmentId);
    }

}
