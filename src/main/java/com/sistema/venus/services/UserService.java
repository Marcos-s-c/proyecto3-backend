package com.sistema.venus.services;

import com.sistema.venus.domain.ResetContraRequestBody;
import com.sistema.venus.domain.User;
import com.sistema.venus.domain.UserStatus;
import com.sistema.venus.repo.UserRepository;
import com.sistema.venus.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRol())
                .build();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long userId){
        return userRepository.findUserByUser_id(userId);
    }

    public Long getIdByEmail(String email){
        return userRepository.findIdByEmail(email);
    }
    public boolean isEmailInUse(String email) {
        User existingUser = userRepository.findUserByEmail(email);
        return existingUser != null;
    }

    public boolean isUserActive(String email) {
        User user = userRepository.findUserByEmail(email);
        return user != null && user.getActive();
    }

    public User findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    public void actualizar(User user) {
        // Verificar si el usuario realmente existe
        User existingUser = userRepository.findUserByEmail(user.getEmail());
        if (existingUser == null) {
            throw new IllegalArgumentException("Usuario no encontrado para actualizar");
        }

        if(user.getPassword().equals("") || user.getPassword().equals(" ") || user.getPassword() == null){
            System.out.println("dentro if");
            user.setPassword(existingUser.getPassword());
        }else if(user.getPassword() != null){
            System.out.println("dentro else");
            existingUser.setPassword(Utils.passwordEncoder(user.getPassword()));
        }

        // Actualizar solo si los campos no son nulos
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }



        try {
            // Guardar el usuario actualizado
            userRepository.save(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el usuario", e);
        }
    }


    public String concuerda(ResetContraRequestBody body) {
        User user = userRepository.findUserByEmail(body.getEmail());
        String contraReal = Utils.passwordDecoder(user.getPassword());
        if(body.getString().equals(contraReal)) return "true";
        else return "false";
    }

    public User getLoggedUser(){
        return userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public List<User> getAllUsers(){
        List<User> users = userRepository.findUsers();
        users.forEach(user -> user.setPassword(null));
        return users;
    }


    public int changeUserStatus(UserStatus body){
        return userRepository.changeUserStatus(body.getUser_id(), body.getActive());
    }

    public List<User> searchUser(String userSearch) {
        List<User> users = userRepository.findUserByNameEmailOrPhone(userSearch);
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
