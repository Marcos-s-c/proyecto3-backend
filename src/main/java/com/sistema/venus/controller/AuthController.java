package com.sistema.venus.controller;

import com.sistema.venus.domain.*;
import com.sistema.venus.services.AuthService;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.Collections;
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
            LoginResponse loginRes = authService.getToken(loginRequest);

            if (!userService.isUserActive(loginRequest.getEmail())) {
                // Usuario no encontrado o no activo
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "A sido baneado del sistema"));
            }

            //UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
            User user = userService.findUserByEmail(loginRequest.getEmail());
            user.setPassword(null);

            // Combina la respuesta de inicio de sesión con los detalles del usuario en un mapa
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", loginRes.getToken());
            responseMap.put("user", user);

            return ResponseEntity.ok(responseMap);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Credenciales inválidas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Ocurrió un error en el servidor"));
        }
    }

    @PostMapping(value = "register")
    public ResponseEntity<Object> register(@RequestBody User user) {
        try {
            return authService.registerUser(user);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Ocurrió un error en el servidor"));
        }
    }


    @PostMapping (value = "/enviarCorreoReset")
    public ResponseEntity<Map<String, String>> enviarCorreoReset(@RequestBody RecuperaContraReqBody body) {
        try {
            authService.sendEmail(body);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "An Error has occurred sending the password reset email"));
        }
    }


    @PostMapping(value = "/recuperarContra")
    public ResponseEntity<Map<String, String>> recuperarContra(@RequestBody PasswordResetChangeRequest body) {
        try {
            authService.passwordChange(body);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Success");
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An Error has occurred sending the password reset email"));
        }
    }

}
