package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Branch;
import kz.kdlolymp.termocontainers.entity.Container;
import kz.kdlolymp.termocontainers.entity.ContainerNote;
import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.repositories.ContainerRepository;
import kz.kdlolymp.termocontainers.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContainerService {

    @PersistenceContext
    private EntityManager manager;
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

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

    public List<Container> findAll() {
        return containerRepository.findAll();
    }
    public List<Container> findAllOrdered() {
        List<Container> containers = new ArrayList<>();
        try{
            containers = manager.createQuery("SELECT c FROM Container c JOIN Department d ON c.department.id=d.id WHERE c.releaseDate=null ORDER BY c.department.branch.id ASC", Container.class).getResultList();
        } catch (NoResultException ex){}
        return containers;
    }

    public void deleteContainer(int id){
        Container container = containerRepository.findContainerById(id);
        containerRepository.delete(container);
    }

    public List<Container> findAllByBranchId(int branchId){
        List<Container> containers = new ArrayList<>();
        if(branchId>1){
            List<Department> departments = departmentRepository.findAllByBranchId(branchId);
            for(Department department: departments){
                List<Container> containerList = containerRepository.findAllByDepartmentId(department.getId());
                for(int i=0; i<containerList.size(); i++){
                    containers.add(containerList.get(i));
                }
            }
        } else {
            containers = findAll();
        }
        return containers;
    }
}
