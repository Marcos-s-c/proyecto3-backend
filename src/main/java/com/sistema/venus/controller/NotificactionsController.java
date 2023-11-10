package com.sistema.venus.controller;


import com.sistema.venus.domain.Notification;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/rest/notifications")
public class NotificactionsController {

    @Autowired
    private NotificationsRepository notificationsRepository;

    @PostMapping(value = "create")
    public ResponseEntity<Object> createNotification(@RequestBody Notification notifications) {
        try{
            notificationsRepository.save(notifications);
            Map<String,Boolean> map = new HashMap<>();
            map.put("Success",true);
            return ResponseEntity.ok(map);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    /*@GetMapping
    public ResponseEntity<Object> getNotifications(){
        return
    }*/


}
