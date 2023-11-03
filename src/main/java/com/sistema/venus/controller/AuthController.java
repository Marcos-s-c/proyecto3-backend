package com.sistema.venus.controller;

import com.sistema.venus.domain.*;
import com.sistema.venus.services.AuthService;
import com.sistema.venus.services.UserService;
import com.sistema.venus.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping("/rest/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest)  {

        try {
            LoginResponse loginRes = authService.getToken(loginRequest);

            return ResponseEntity.ok(loginRes);

        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    @PostMapping(value = "register")
    public ResponseEntity register(@RequestBody User user)  {
        try {
            user.setRol(Constants.USER_ROLE);
            return ResponseEntity.ok(userService.saveUser(user));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "/enviarCorreoReset")
    public ResponseEntity<String> enviarCorreoReset(@RequestBody RecuperaContraReqBody body) {
        try{
            return authService.sendEmail(body);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("An Error has occurred sending the password reset email");
        }
    }

    @GetMapping(value = "/recuperarContra")
    public ResponseEntity<String> recuperarContra(@RequestBody PasswordResetChangeRequest body) {
        try{
            authService.passwordChange(body);
            return ResponseEntity.ok("Success");
        }
        catch (ValidationException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("An Error has occurred sending the password reset email");
        }
    }
}
