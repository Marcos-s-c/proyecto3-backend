package com.sistema.venus.controller;

import com.sistema.venus.domain.Notificaciones;
import com.sistema.venus.domain.ResetContraRequestBody;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.UserRepository;
import com.sistema.venus.services.PreferenciasNotificacionesService;
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

    @Autowired
    PreferenciasNotificacionesService prefNotService;

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

    @PostMapping(value = "concordar")
    public ResponseEntity<String> concordar(@RequestBody ResetContraRequestBody body){
        try{
            //userService.concuerda(body);
            return ResponseEntity.ok(userService.concuerda(body));
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(value = "preferencias")
    public ResponseEntity<Notificaciones> addPreferencia(@RequestBody Notificaciones body){
        System.out.println("pref"+body);
        try{
            return ResponseEntity.ok(prefNotService.addPrefNotificacion(body));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

}
