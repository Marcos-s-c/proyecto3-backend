package com.sistema.venus.controller;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.*;
import com.sendgrid.helpers.mail.objects.*;
import com.sistema.venus.repo.UserRepository;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping(value = "authTest")
    public ResponseEntity<String> getUser(){
        try{
            return ResponseEntity.of(Optional.of("Success"));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

}
