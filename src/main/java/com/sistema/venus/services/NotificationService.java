package com.sistema.venus.services;

import com.sistema.venus.domain.Notification;
import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.NotificationsRepository;
import com.sistema.venus.repo.UserRepository;
import com.sistema.venus.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private UserRepository userRepository;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void createPeriodCriteriaNotification(PeriodCriteria periodCriterias){

        if(periodCriterias.getFieldName().equals("periodColor")){
            Notification notification = new Notification();
            User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            if(notification.getDate()==null){
                notification.setDate(Utils.getDateCurrentTimezone());
            }
            notification.setText(createNotificationText(periodCriterias.getValue()));
            notification.setUser_id(user);
            notification.setOpen(false);
            notificationsRepository.save(notification);
        }


    }

    public List<Notification> getNotifications(){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        return notificationsRepository.getNotificationByUserId(user.getUser_id());
    }

    public void readNotifications(){
        notificationsRepository.findAll().forEach(notification -> {
            notification.setOpen(true);
            notificationsRepository.save(notification);
        });
    }

    public String createNotificationText(String color) {
        String text = "";
        switch (color) {
            case "rojo palido":
                text = "Su sangrado fue de color rojo pálido, " +
                        "lo cual puede estar relacionado a la toma de algún anticonceptivo o transtornos hormonales.  " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza. ";
                break;

            case "anaranjado":
                text = "Su sangrado fue de color naranja, lo cual puede estar relacionado infecciones. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza. ";
                break;
            case "Otro":
                text = "Su sangrado fue de un color diferente al rojo.  Es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
                break;

        }
        return text;
    }
}
