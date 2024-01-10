package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.ContainerValue;
import kz.kdlolymp.termocontainers.repositories.ContainerValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContainerValueService {

    @Autowired
    private ContainerValueRepository valueRepository;
    @Autowired
    private ContainerNoteService containerNoteService;

    public List<ContainerValue> findAll(){
        return valueRepository.findAll();
    }

    public ContainerValue findByValueName(String valueName){
        return valueRepository.findContainerValueByValueName(valueName);
    }
    public boolean saveValue(ContainerValue value){
        valueRepository.save(value);
        return true;
    }
    public boolean addNewValue(ContainerValue value){
        ContainerValue valueFromDb = findByValueName(value.getValueName());
        if(valueFromDb!=null){
            return false;
        } else{
            valueRepository.save(value);
            return true;
        }
    }
    public ContainerValue findContainerValueById(int id){
        return valueRepository.findContainerValueById(id);
    }

    public void deleteContainerValue(int id){
        ContainerValue value = valueRepository.findContainerValueById(id);
        valueRepository.delete(value);
    }
}
