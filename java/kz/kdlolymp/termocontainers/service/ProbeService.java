package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Branch;
import kz.kdlolymp.termocontainers.entity.Container;
import kz.kdlolymp.termocontainers.entity.Probe;
import kz.kdlolymp.termocontainers.entity.TimeStandard;
import kz.kdlolymp.termocontainers.repositories.ProbeRepository;
import kz.kdlolymp.termocontainers.repositories.TimeStandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProbeService {

    @Autowired
    private ProbeRepository probeRepository;
    @Autowired
    private TimeStandardService standardService;

    public Probe findByProbeName(String probeName){
        return probeRepository.findProbeByProbeName(probeName);
    }
    public boolean addNewProbe(Probe probe){
        Probe probeFromDb = findByProbeName(probe.getProbeName());
        if(probeFromDb!=null){
            return false;
        } else {
            probeRepository.save(probe);
            return true;
        }
    }

    public List<Probe> findAll() {
        return probeRepository.findAll();
    }

    public void deleteProbe(int id){
        Probe probe = probeRepository.findProbeById(id);
        List<TimeStandard> standards = standardService.findAllByProbeId(id);
        for(TimeStandard standard: standards){
            standardService.deleteTimeStandard(standard.getId());
        }
        probeRepository.delete(probe);
    }

}
