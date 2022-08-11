package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.AlarmGroup;
import kz.kdlolymp.termocontainers.entity.ContainerNote;
import kz.kdlolymp.termocontainers.entity.User;
import kz.kdlolymp.termocontainers.repositories.AlarmGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class AlarmGroupService {
    @Autowired
    private AlarmGroupRepository alarmGroupRepository;
    @Autowired
    private EntityManager manager;

    public List<AlarmGroup> findAll(){
        return alarmGroupRepository.findAll();
    }
    public boolean saveAlarmGroup(AlarmGroup alarmGroup){
        if(alarmGroupRepository.save(alarmGroup)!=null){
            return true;
        } else {
            return false;
        }
    }
    public boolean addNewAlarmGroup(AlarmGroup alarmGroup){
        List<User> users = new ArrayList<User>();
        alarmGroup.setUsers(users);
        if(alarmGroupRepository.save(alarmGroup)!=null){
            return true;
        } else {
            return false;
        }
    }
    public boolean deleteAlarmGroup(int id){
        alarmGroupRepository.deleteById(id);
        return true;
    }
    public AlarmGroup findAlarmGroupById(int id){
        return alarmGroupRepository.findAlarmGroupById(id);
    }
    public AlarmGroup findAlarmGroupByName(String alarmGroupName){
        return alarmGroupRepository.findAlarmGroupByAlarmGroupName(alarmGroupName);
    }
    public List<User> getUsersById(int id){
        List<User> users = new ArrayList<>();
        try {
            AlarmGroup alarmGroup = manager.createQuery("SELECT ag FROM AlarmGroup ag JOIN FETCH ag.users WHERE ag.id = :paramId"
                    , AlarmGroup.class).setParameter("paramId", id).getSingleResult();
            if(alarmGroup!=null){
                users = alarmGroup.getUsers();
            }
        } catch (NoResultException ex){}
        return users;
    }

    public boolean addNewUserToAlarmGroup(User user, int alarmGroupId){
        AlarmGroup alarmGroup = findAlarmGroupById(alarmGroupId);
        List<User> users = alarmGroup.getUsers();
        boolean isExist = false;
        if(users!=null && users.size()>0) {
            for (int i = 0; i < users.size(); i++) {
                if(users.get(i).getId() == user.getId()){
                    isExist = true;
                }
            }
        }
        if(isExist){
            return false;
        } else {
            users.add(user);
            alarmGroup.setUsers(users);
            alarmGroupRepository.save(alarmGroup);
            return true;
        }
    }

    public boolean removeUserFromAlarmGroup(User user, int alarmGroupId){
        AlarmGroup alarmGroup = findAlarmGroupById(alarmGroupId);
        List<User> users = alarmGroup.getUsers();
        List<User> newUsersList = new ArrayList<>();
        if(users!=null && users.size()>0){
            for(int i=0; i<users.size(); i++){
                User userFromList = users.get(i);
                if(userFromList.getId()==user.getId()){
                } else{
                    newUsersList.add(userFromList);
                }
            }
        }
        alarmGroup.setUsers(newUsersList);
        AlarmGroup savedGroup = alarmGroupRepository.save(alarmGroup);
        if(savedGroup!=null){
            return true;
        } else {
            return false;
        }
    }


}
