package com.sistema.venus.services;

import com.sistema.venus.domain.LoginRequest;
import com.sistema.venus.domain.LoginResponse;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.UserRepository;
import com.sistema.venus.util.Constants;
import com.sistema.venus.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRol())
                .build();}

    public LoginResponse getToken(LoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        String email = authentication.getName();
        User user = new User(email);
        String token = jwtUtil.createToken(user);
        return new LoginResponse(token);
    }

    public User createUser(User user) {
        user.setRol(Constants.USER_ROLE);
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
}
