package com.sistema.venus.controller;


import com.sistema.venus.domain.Notification;
import com.sistema.venus.repo.NotificationsRepository;
import com.sistema.venus.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/rest/notifications")
public class NotificactionsController {

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private NotificationService notificationsService;

   /* @PostMapping(value = "create")
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
    }*/

    /*@GetMapping(value )
    public ResponseEntity<Object> getNotifications(){
        return
    }*/

    @GetMapping("getAllPosts")
    public List<Notification> getAllNotifications() {
        try {
            return notificationsService.getNotifications();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
