package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.TimeStandard;
import kz.kdlolymp.termocontainers.repositories.TimeStandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeStandardService {

    @Autowired
    private TimeStandardRepository standardRepository;

    public List<TimeStandard> findAll() {
        return standardRepository.findAll();
    }

    public List<TimeStandard> findAllByProbeId(int probeId){
        return standardRepository.findAllByProbeId(probeId);
    }

    public List<TimeStandard> findAllByFirstPointId(int firstPointId){ return standardRepository.findAllByFirstPointId(firstPointId); }
    public TimeStandard findByParameters(int firstPointId, int secondPointId, int probeId){
        TimeStandard standard = new TimeStandard();
        List<TimeStandard> standards;
        standards = findAllByFirstPointId(firstPointId);
        boolean isFound = false;
        for(int i=0; i<standards.size(); i++){
            TimeStandard firstStandard = standards.get(i);
            if(firstStandard.getSecondPointId() == secondPointId &&
                firstStandard.getProbeId()==probeId){
                isFound = true;
                standard = firstStandard;
            }
        }
        if(!isFound){
            standards = findAllByFirstPointId(secondPointId);
            for(int j=0; j<standards.size(); j++){
                TimeStandard secondStandard = standards.get(j);
                if(secondStandard.getSecondPointId() == firstPointId &&
                        secondStandard.getProbeId() == probeId){
                    standard = secondStandard;
                }
            }
        }
        return standard;
    }
    public void deleteTimeStandard(int id){

    }

}
