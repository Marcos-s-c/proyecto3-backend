package com.sistema.venus.controller;

import com.sistema.venus.domain.LoginRequest;
import com.sistema.venus.domain.LoginResponse;
import com.sistema.venus.domain.User;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @PostMapping(value = "login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest)  {

        try {
            LoginResponse loginRes = authService.getToken(loginRequest);

            if (!userService.isUserActive(loginRequest.getEmail())) {
                // Usuario no encontrado o no activo
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "A sido baneado del sistema"));
            }

            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());

            return ResponseEntity.ok(loginRes);

            return ResponseEntity.ok(responseMap);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Credenciales inválidas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Ocurrió un error en el servidor"));
        }
    }


    @PostMapping(value = "register")
    public ResponseEntity register(@RequestBody User user)  {
        try {
            user.setRol(Constants.USER_ROLE);
            user.setActive(true);
            User savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Ocurrió un error en el servidor"));
        }
    }
}
