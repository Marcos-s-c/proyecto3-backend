package com.sistema.venus.controller;

import com.sistema.venus.domain.User;
import com.sistema.venus.repo.UserRepository;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(value = "logout")
    public ResponseEntity<Object> logout(){
        try{
            SecurityContextHolder.clearContext();
            Map<String,Boolean> map = new HashMap<>();
            map.put("Success",true);
            return ResponseEntity.of(Optional.of(map));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping(value = "actualizar")
    public ResponseEntity<User> actualizar(@RequestBody User u){
        try {
            userService.actualizar(u);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

}
