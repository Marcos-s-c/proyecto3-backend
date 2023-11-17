package com.sistema.venus.services;

import com.sistema.venus.domain.Notification;
import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.NotificationsRepository;
import com.sistema.venus.repo.PeriodCriteriaRepository;
import com.sistema.venus.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private PeriodCriteriaRepository periodCriteriaRepository;


    public void createPeriodCriteriaNotification(PeriodCriteria periodCriterias){


        periodColorNotification(periodCriterias);
        fluidColorNotification(periodCriterias);
        threeMonthsExcessiveBleedingNotification(periodCriterias);
    }

    private void threeMonthsExcessiveBleedingNotification(PeriodCriteria periodCriterias) {
        User user = userService.getLoggedUser();

        String text = "Su menstruación ha sido abundante durante los últimos 3 meses." +
                " Un sangrado abundante puede ser indicador de problemas hormonales, miomas uterinos, patología endometrial, entre otras. \" +\n" +
                " Además, el sangrado excesivo prolongado puede desencadenar una anemia, por lo que es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
        if(periodCriterias.getValue().equals("Muy abundante") && notificationsRepository.getNotificationByDateAndTextAndUser_id(Utils.getDateCurrentTimezone(),
                text,
                user.getUser_id())  == null){
            int count = 1;
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
                notification.setText(text);
                notificationsRepository.save(notification);
            }
        }
    }

    private void fluidColorNotification(PeriodCriteria periodCriterias) {
        String text = createNotificationFlujo(periodCriterias.getValue());
        User user = userService.getLoggedUser();
        if(periodCriterias.getFieldName().equals("fluidColor") &&
                Arrays.asList("gris", "amarillo", "verde", "transparente").contains(periodCriterias.getValue()) &&
                notificationsRepository.getNotificationByDateAndTextAndUser_id(Utils.getDateCurrentTimezone(),
                        text,
                        user.getUser_id())  == null)
        {
            Notification notification = new Notification();
            if(notification.getDate()==null){
                notification.setDate(Utils.getDateCurrentTimezone());
            }
            notification.setUser_id(user);
            notification.setText(text);
            notification.setOpen(false);

            notificationsRepository.save(notification);

        }
    }

    private void periodColorNotification(PeriodCriteria periodCriterias) {
        String text = createNotificationColor(periodCriterias.getValue());
        User user = userService.getLoggedUser();
        if(periodCriterias.getFieldName().equals("periodColor") &&
                Arrays.asList("rojo palido", "anaranjado", "Otro").contains(periodCriterias.getValue())&&
                notificationsRepository.getNotificationByDateAndTextAndUser_id(Utils.getDateCurrentTimezone(),
                        text,
                        user.getUser_id())  == null){

            Notification notification = new Notification();

            if(notification.getDate()==null){
                notification.setDate(Utils.getDateCurrentTimezone());
            }
            notification.setUser_id(user);
            notification.setOpen(false);
            notification.setText(text);
            notificationsRepository.save(notification);

        }
    }

    public List<Notification> getNotifications(){
        User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
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
    public String createNotificationFlujo(String color) {
        String text = "";
        switch (color) {
            case "gris":
                text = "Su flujo cervical/vaginal fue de color gris, puede ser producto de una vaginosis. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza. ";
                break;

            case "amarillo":
                text = "Su flujo cervical/vaginal fue de color amarillo, puede ser producto de una infección ginecológica. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
                break;
            case "verde":
                text = "Su flujo cervical/vaginal fue de color verde, puede ser producto de una infección ginecológica. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
                break;
            case "transparente":
                text = "Su flujo cervical/vaginal fue de un color diferente al blanco o transparente. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
                break;

        }
        return text;
    }
}
