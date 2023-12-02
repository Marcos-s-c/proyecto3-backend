package com.sistema.venus.controller;

import com.sistema.venus.domain.Notification;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.NotificationsRepository;
import com.sistema.venus.services.NotificationService;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/rest/notifications")
public class NotificactionsController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationsService;

    @GetMapping("getAllPosts")
    public List<Notification> getAllNotifications() {
        try {
            return notificationsService.getNotifications();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("readNotifications")
    public ResponseEntity<Object> readAll() {
        try {
            notificationsService.readNotifications();
            Map<String, Object> map = new HashMap<>();
            map.put("Success",true);
            return ResponseEntity.ok().body(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("sendMonthlyReport")
    public ResponseEntity<Object> sendMonthlyReport() throws MessagingException, IOException, ValidationException {
        try {
            User user = userService.getLoggedUser();
            notificationsService.sendReportEmail(user);
            Map<String, Object> map = new HashMap<>();
            map.put("Success",true);
            return ResponseEntity.ok().body(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
