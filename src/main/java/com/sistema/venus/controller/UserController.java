package com.sistema.venus.controller;

import com.sistema.venus.repo.UserRepository;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/rest/users")
public class UserController {

    @GetMapping(value = "logout")
    public ResponseEntity<String> logout(){
        try{
            SecurityContextHolder.clearContext();
            return ResponseEntity.of(Optional.of("Success"));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
