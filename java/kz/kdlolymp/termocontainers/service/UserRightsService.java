package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.User;
import kz.kdlolymp.termocontainers.entity.UserRights;
import kz.kdlolymp.termocontainers.repositories.UserRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
public class UserRightsService {

    @Autowired
    private UserRightsRepository userRightsRepository;
    @Autowired
    private EntityManager manager;

    public UserRights findUserGroupById(int userGroupId){
        return userRightsRepository.findUserGroupById(userGroupId);
    }
    public UserRights findGroupByUser(User user){
        return manager.createQuery("SELECT u FROM UserRights u WHERE u.user =:userParameter", UserRights.class)
                .setParameter("userParameter", user).getSingleResult();
    }

    public UserRights addNewUserGroup(UserRights userRights){
        userRights = userRightsRepository.save(userRights);
        userRightsRepository.flush();
        return userRights;
    }
}
