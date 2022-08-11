package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.BetweenPoint;
import kz.kdlolymp.termocontainers.repositories.BetweenPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BetweenPointService {

    @Autowired
    BetweenPointRepository pointRepository;

    public BetweenPoint findPointById(Long id){
        return pointRepository.findBetweenPointById(id);
    }

    public boolean addNewPoint(BetweenPoint betweenPoint){
        if(findPointById(betweenPoint.getId())!=null){
            return false;
        } else{
            pointRepository.save(betweenPoint);
            return true;
        }
    }
    public List<BetweenPoint> getAllPointsByNoteId(Long containerNoneId){
        return pointRepository.findAllByContainerNoteId(containerNoneId);
    }

}
