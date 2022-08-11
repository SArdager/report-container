package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.UserRights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRightsRepository extends JpaRepository<UserRights, Integer> {

    UserRights findUserGroupById(int userGroupId);

}
