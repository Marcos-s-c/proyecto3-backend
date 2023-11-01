package com.sistema.venus.controller;

import com.sistema.venus.domain.LoginRequest;
import com.sistema.venus.domain.LoginResponse;
import com.sistema.venus.domain.User;
import com.sistema.venus.services.AuthService;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rest/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {

        try {
            // Autenticar al usuario
            LoginResponse loginRes = authService.getToken(loginRequest);

            // Cargar información adicional del usuario
            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());

            // Combina la respuesta de inicio de sesión con los detalles del usuario en un mapa
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", loginRes.getToken());
            responseMap.put("user", userDetails);

            return ResponseEntity.ok(responseMap);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(value = "register")
    public ResponseEntity register(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.createUser(user));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
