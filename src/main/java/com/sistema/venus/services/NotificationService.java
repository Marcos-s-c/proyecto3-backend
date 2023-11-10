package com.sistema.venus.services;

import com.sistema.venus.domain.Notification;
import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.NotificationsRepository;
import com.sistema.venus.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private UserRepository userRepository;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Notification saveNotifcation(Notification notification){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(notification.getDate()==null){
            ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
            ZoneId zId = ZoneId.of("US/Central");
            notification.setDate(LocalDateTime.ofInstant(zdt.toInstant(), zId).toLocalDate());
        }
        notification.setUser_id(user);
        return notificationsRepository.save(notification);
    }

    public void createPeriodCriteriaNotification(PeriodCriteria periodCriterias){
        System.out.println(periodCriterias.getFieldName());
        System.out.println(periodCriterias.getValue());
        if(periodCriterias.getFieldName().equals("periodColor")){
            System.out.println("paso");
        }

        //saveNotifcation()
    }

    public List<Notification> getNotifications(){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        return notificationsRepository.getNotificationByUserId(user.getUser_id());
    }
}
