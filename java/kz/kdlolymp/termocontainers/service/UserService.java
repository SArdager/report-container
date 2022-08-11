package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.User;
import kz.kdlolymp.termocontainers.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
@Component
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Autowired
    private EntityManager manager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user != null && user.isEnabled()){
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
            return userDetails;
        } else {
            throw new UsernameNotFoundException("User: " + username + " - not found");
        }
    }

    public User findUserById(Long userId){
        return userRepository.findUserById(userId);
    }
    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public List<User> allUsers(){
        return userRepository.findAll();
    }

    public boolean saveUser(User user){
        userRepository.save(user);
        return true;
    }
    public boolean addNewUser(User user){
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if(userFromDB!= null){
            return false;
        }
        user.setPassword(passwordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean changePassword(User user){
        String password = user.getPassword();
        user.setPassword(passwordEncoder().encode(password));
        userRepository.save(user);
        return true;
    }
    public List<User> getUsersByPartUsername(String text) {
        return manager.createQuery("SELECT u FROM User u WHERE u.userSurname LIKE :text", User.class)
                .setParameter("text", text).setMaxResults(20).getResultList();
    }

    public List<User> findUsersByName(String surname, String firstname) {
        List<User> users = new ArrayList<>();
        if(surname.length()>0 && firstname.length()>0){
            users = manager.createQuery("SELECT u FROM User u WHERE u.userSurname LIKE :surname AND u.userFirstname LIKE :firstname", User.class)
                    .setParameter("surname", surname).setParameter("firstname", firstname).setMaxResults(20).getResultList();
        } else if(surname.length()>0){
            users = manager.createQuery("SELECT u FROM User u WHERE u.userSurname LIKE :surname", User.class)
                    .setParameter("surname", surname).setMaxResults(20).getResultList();
        } else {
            users = manager.createQuery("SELECT u FROM User u WHERE u.userFirstname LIKE :firstname", User.class)
                    .setParameter("firstname", firstname).setMaxResults(20).getResultList();
        }
        return users;
    }

}
