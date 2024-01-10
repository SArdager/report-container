package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.ContainerValue;
import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.entity.TimeStandard;
import kz.kdlolymp.termocontainers.repositories.TimeStandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeStandardService {

    @Autowired
    private TimeStandardRepository standardRepository;
    @Autowired
    private DepartmentService  departmentService;

    public TimeStandard findById(int id) {
        return standardRepository.findById(id);
    }

    public List<TimeStandard> findAll() {
        return standardRepository.findAll();
    }

    public boolean save(TimeStandard standard) {
        standardRepository.save(standard);
        return true;
    }

    public List<TimeStandard> findAllByFirstPointId(int firstPointId){ return standardRepository.findAllByFirstPointId(firstPointId); }
    public TimeStandard findByParameters(int firstPointId, int secondPointId){
        TimeStandard standard = new TimeStandard();
        List<TimeStandard> standards;
        standards = findAllByFirstPointId(firstPointId);
//        boolean isFound = false;
        for(int i=0; i<standards.size(); i++){
            if(standards.get(i).getSecondPointId() == secondPointId){
//                isFound = true;
                standard = standards.get(i);
            }
        }
//        if(!isFound){
//            Department firstDepartment = departmentService.findDepartmentById(firstPointId);
//            Department secondDepartment = departmentService.findDepartmentById(secondPointId);
//            if(firstDepartment.getBranch().getId() == secondDepartment.getBranch().getId()){
//                standards = findAllByFirstPointId(secondPointId);
//                for(int j=0; j<standards.size(); j++){
//                    if(standards.get(j).getSecondPointId() == firstPointId){
//                        standard = standards.get(j);
//                    }
//                }
//            }
//        }
        return standard;
    }

}
