package com.sistema.venus.services;

import com.sistema.venus.domain.Notification;
import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.NotificationsRepository;
import com.sistema.venus.repo.PeriodCriteriaRepository;
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
    boolean save = false;
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PeriodCriteriaRepository periodCriteriaRepository;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void createPeriodCriteriaNotification(PeriodCriteria periodCriterias){

        if(periodCriterias.getFieldName().equals("periodColor")){
            Notification notification = new Notification();
            User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            if(notification.getDate()==null){
                notification.setDate(Utils.getDateCurrentTimezone());
            }
            notification.setUser_id(user);
            notification.setOpen(false);
            notification.setText(createNotificationColor(periodCriterias.getValue()));
            if(save == true){
                notificationsRepository.save(notification);
                save = false;
            }

        }

        if(periodCriterias.getFieldName().equals("fluidColor")){
            Notification notification = new Notification();
            User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            if(notification.getDate()==null){
                notification.setDate(Utils.getDateCurrentTimezone());
            }
            notification.setUser_id(user);
            notification.setText(createNotificationFlujo(periodCriterias.getValue()));
            notification.setOpen(false);
            if(save == true){
                notificationsRepository.save(notification);
                save = false;
            }
        }

        if(periodCriterias.getValue().equals("Muy abundante")){
            int count = 1;
            User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            List<PeriodCriteria> previousCriterias = periodCriteriaRepository.findByUserIdAndDateBetween(user.getUser_id(), Utils.getDateCurrentTimezone().minusMonths(2),Utils.getDateCurrentTimezone());

            for (int i = 0; i < previousCriterias.size(); i++) {
                if( previousCriterias.get(i).getValue().equals("Muy abundante")){
                    count++;
                }
            }

            if(count >= 3){
                Notification notification = new Notification();
                if(notification.getDate()==null){
                    notification.setDate(Utils.getDateCurrentTimezone());
                }
                notification.setUser_id(user);
                notification.setText("Su menstruación ha sido abundante durante los últimos 3 meses. " +
                        "Un sangrado abundante puede ser indicador de problemas hormonales, miomas uterinos, patología endometrial, entre otras. " +
                        "Además, el sangrado excesivo prolongado puede desencadenar una anemia, por lo que es importante que consulte a su médico de cabecera o ginecólogo de confianza.");
                notificationsRepository.save(notification);
            }
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

    public String createNotificationColor(String color) {
        String text = "";
        switch (color) {
            case "rojo palido":
                text = "Su sangrado fue de color rojo pálido, " +
                        "lo cual puede estar relacionado a la toma de algún anticonceptivo o transtornos hormonales.  " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza. ";
                save = true;
                break;

            case "anaranjado":
                text = "Su sangrado fue de color naranja, lo cual puede estar relacionado infecciones. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza. ";
                save = true;
                break;
            case "Otro":
                text = "Su sangrado fue de un color diferente al rojo.  Es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
                save = true;
                break;

        }
        return text;
    }
    public String createNotificationFlujo(String color) {
        String text = "";
        switch (color) {
            case "gris":
                text = "Su flujo cervical/vaginal fue de color gris, puede ser producto de una vaginosis. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza. ";
                save = true;
                break;

            case "amarillo":
                text = "Su flujo cervical/vaginal fue de color amarillo, puede ser producto de una infección ginecológica. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
                save = true;
                break;
            case "verde":
                text = "Su flujo cervical/vaginal fue de color verde, puede ser producto de una infección ginecológica. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
                save = true;
                break;
            case "transparente":
                text = "Su flujo cervical/vaginal fue de un color diferente al blanco o transparente. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
                save = true;
                break;

        }
        return text;
    }
}
