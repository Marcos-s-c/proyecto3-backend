package com.sistema.venus.services;

import com.sistema.venus.domain.User;
import com.sistema.venus.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    public String getIdByEmail(String email){
        return userRepository.findIdByEmail(email);
    }
}
