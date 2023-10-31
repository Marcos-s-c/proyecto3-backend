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
            LoginResponse loginRes = userService.getToken(loginRequest);

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
            return ResponseEntity.ok(userService.createUser(user));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
