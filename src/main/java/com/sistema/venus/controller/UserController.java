package com.sistema.venus.controller;

import com.sistema.venus.domain.UserPreferences;
import com.sistema.venus.domain.ResetContraRequestBody;
import com.sistema.venus.domain.User;
import com.sistema.venus.domain.UserStatus;
import com.sistema.venus.services.UserPreferenceService;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserPreferenceService prefNotService;

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

    @PostMapping(value = "preferencias",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserPreferences> addPreferencia(@RequestBody UserPreferences body){
        try{
            return ResponseEntity.ok(prefNotService.addPrefNotificacion(body));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(value = "frecuencias",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserPreferences> addFrecuencia(@RequestBody UserPreferences body){
        try{
            return ResponseEntity.ok(prefNotService.addFrecuencia(body));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    @GetMapping(value = "preferencias/{email}")
    public ResponseEntity<UserPreferences> getPreferenciaNotificacionByEmail(@PathVariable(value = "email")String email){
        try{
            return ResponseEntity.ok(prefNotService.getPreferenciaNotificacionByEmail(email));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(name = "search", required = false) String userSearch){
        try{
            if(userSearch != null && !userSearch.isEmpty()){
                return ResponseEntity.ok(userService.searchUser(userSearch));
            }
            return ResponseEntity.ok(userService.getAllUsers());
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping(value = "/changeStatus")
    public ResponseEntity<Integer> changeUserStatus(@RequestBody UserStatus userStatus){
        try{
            return ResponseEntity.ok(userService.changeUserStatus(userStatus));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

}
