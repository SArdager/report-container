package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.entity.User;
import kz.kdlolymp.termocontainers.entity.UserRights;
import kz.kdlolymp.termocontainers.repositories.UserRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class UserRightsService {

    @Autowired
    private UserRightsRepository userRightsRepository;
    @Autowired
    private EntityManager manager;

    public List<UserRights> findRightsByUser(Long userId){
        return manager.createQuery("SELECT ur FROM UserRights ur WHERE ur.user.id =:userParameter", UserRights.class)
                .setParameter("userParameter", userId).getResultList();
    }

    public UserRights addNewUserRights(UserRights userRights){
        userRights = userRightsRepository.save(userRights);
        userRightsRepository.flush();
        return userRights;
    }
    public UserRights getUserRights(User user, Department department){
        return manager.createQuery("SELECT r FROM UserRights r WHERE r.user = :userParameter AND r.department = :depParameter",
                UserRights.class).setParameter("userParameter", user).setParameter("depParameter", department).setMaxResults(1).getSingleResult();
    }

    public boolean deleteUserRights(UserRights userRights){
        userRightsRepository.delete(userRights);
        userRightsRepository.flush();
        return true;
    }
    public boolean changeUserRights(UserRights userRights){
        userRightsRepository.save(userRights);
        userRightsRepository.flush();
        return true;
    }

}
