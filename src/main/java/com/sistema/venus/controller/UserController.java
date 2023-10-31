package com.sistema.venus.controller;

import com.sistema.venus.domain.User;
import com.sistema.venus.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
