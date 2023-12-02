package com.sistema.venus.services;

import com.sistema.venus.domain.LoginRequest;
import com.sistema.venus.domain.Notification;
import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.NotificationsRepository;
import com.sistema.venus.repo.PeriodCriteriaRepository;
import com.sistema.venus.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PeriodCriteriaRepository periodCriteriaRepository;
    @Value("${temp.folder}")
    private String tempFolder;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JavaMailSender javaMailSender;


    public void createPeriodCriteriaNotification(PeriodCriteria periodCriterias){


        periodColorNotification(periodCriterias);
        fluidColorNotification(periodCriterias);
        threeMonthsExcessiveBleedingNotification(periodCriterias);
        periodExtension(periodCriterias);
        lastPeriod(periodCriterias);
        periodVariation(periodCriterias);
    }

    private void threeMonthsExcessiveBleedingNotification(PeriodCriteria periodCriterias) {
        User user = userService.getLoggedUser();

        String text = "Su menstruación ha sido abundante durante los últimos 3 meses." +
                " Un sangrado abundante puede ser indicador de problemas hormonales, miomas uterinos, patología endometrial, entre otras. " +
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
                Arrays.asList("gris", "amarillo", "verde", "Otro").contains(periodCriterias.getValue()) &&
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

    private void periodExtension(PeriodCriteria periodCriteria){
        User user = userService.getLoggedUser();
        if(periodCriteria.getValue().equals("fin")){
            long day = 5;
            List<PeriodCriteria> previousCriterias = periodCriteriaRepository.findByUserIdAndDateBetween(user.getUser_id(), periodCriteria.getDate().minusDays(15),periodCriteria.getDate());
            for (int i = previousCriterias.size()-1; i > 0; i--) {
                if(previousCriterias.get(i).getValue().equals("inicio")){
                    Duration duration = Duration.between(previousCriterias.get(i).getDate().atStartOfDay(), periodCriteria.getDate().atStartOfDay());
                    day = duration.getSeconds() / (24 * 60 * 60);
                    i = 0;
                }
            }
            if(day <= 1){
                Notification notification = new Notification();
                if(notification.getDate()==null){
                    notification.setDate(Utils.getDateCurrentTimezone());
                }
                notification.setUser_id(user);
                notification.setText("Su menstruación fue de 1 día. Existen diversas razones por las cuales esta situación puede presentarse, entre las cuales están: " +
                        "efecto secundario de anticonceptivos, efecto secundario de algún otro medicamento, indicios de embarazo, problemas hormonales, problemas con nutrientes de la dieta, entre otras. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza.");
                notificationsRepository.save(notification);
            }
            if(day > 7){
                Notification notification = new Notification();
                if(notification.getDate()==null){
                    notification.setDate(Utils.getDateCurrentTimezone());
                }
                notification.setUser_id(user);
                notification.setText("Su menstruación fue mayor a 7 días. Una duración extensa de la menstruación puede ser indicador de problemas hormonales, miomas uterinos, patología endometrial, entre otras. " +
                        "Además, el sangrado excesivo prolongado puede desencadenar una anemia, por lo que es importante que consulte a su médico de cabecera o ginecólogo de confianza.");
                notificationsRepository.save(notification);
            }
        }
    }

    private void lastPeriod(PeriodCriteria periodCriteria){
        User user = userService.getLoggedUser();
        List<PeriodCriteria> previousCriterias = periodCriteriaRepository.findByUserIdAndDateBetween(user.getUser_id(), periodCriteria.getDate().minusDays(45),periodCriteria.getDate());
        List<PeriodCriteria> allCriterias = periodCriteriaRepository.getPeriodCriteriaByUserId(user.getUser_id());
        if(previousCriterias.size() == 0 && allCriterias.size() >= 1){
            Notification notification = new Notification();
            if(notification.getDate()==null){
                notification.setDate(Utils.getDateCurrentTimezone());
            }
            notification.setUser_id(user);
            notification.setText("Su ciclo femenino superó los 45 días respecto a su último día de menstruación. " +
                    "Existen diversas razones por las cuales el ciclo femenino puede extenderse, entre las cuales están: " +
                    "embarazo, menopausia, perimenopausia, problemas hormonales, problemas con nutrientes de la dieta, entre otras. " +
                    "Es importante que consulte a su médico de cabecera o ginecólogo de confianza.");
            notificationsRepository.save(notification);
        }

    }

    private void periodVariation(PeriodCriteria periodCriteria){
        int count = 0;
        Long periodDays1 = 12345678910L;
        Long periodDays2 = 12345678910L;
        LocalDate criteriaDate = periodCriteria.getDate();
        User user = userService.getLoggedUser();
        List<PeriodCriteria> previousCriterias = periodCriteriaRepository.getPeriodCriteriaByUserId(user.getUser_id());
        if(periodCriteria.getValue().equals("inicio")){
            for (int i = previousCriterias.size()-1; i > 0 ; i--) {
                if(previousCriterias.get(i).getValue().equals("inicio") && count < 1){
                    count ++;
                    criteriaDate = previousCriterias.get(i).getDate();
                    periodDays1 = periodCriteria.getDate().toEpochDay() - previousCriterias.get(i).getDate().toEpochDay();
                    i--;
                }
                if(previousCriterias.get(i).getValue().equals("inicio") && count < 2){
                    count ++;
                    periodDays2 = criteriaDate.toEpochDay() - previousCriterias.get(i).getDate().toEpochDay();
                }
            }
            Long result = periodDays1 - periodDays2;
            if(result >= 9){
                Notification notification = new Notification();
                if(notification.getDate()==null){
                    notification.setDate(Utils.getDateCurrentTimezone());
                }
                notification.setUser_id(user);
                notification.setText("La extensión de su ciclo femenino ha variado en al menos 9 días respecto al anterior. " +
                        " Si estas variaciones se repiten con frecuencia, es importante que consulte a su médico de cabecera o ginecólogo de confianza");
                notificationsRepository.save(notification);
            }
        }

    }

    public List<Notification> getNotifications(){
         User user = userService.getLoggedUser();
         Comparator<Notification> notificationComparator = Comparator.comparing(Notification::getId, Comparator.reverseOrder());
         List<Notification> notifications =  notificationsRepository.getNotificationByUserId(user.getUser_id());
         notifications.sort(notificationComparator);
         return notifications;
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
            case "Otro":
                text = "Su flujo cervical/vaginal fue de un color diferente al blanco o transparente. " +
                        "Es importante que consulte a su médico de cabecera o ginecólogo de confianza.";
                break;

        }
        return text;
    }

    public void sendReportEmail() throws MessagingException, IOException {
        User user = userService.getLoggedUser();
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setTo(user.getEmail());
        helper.setFrom("venus49117413@gmail.com");
        helper.setSubject("Venus");
        helper.setText(String.format("Reporte generado del mes %s", Utils.getDateCurrentTimezone().getMonthValue()));
        File reportPng = getUserReport(user);
        helper.addAttachment(String.format("Reporte-%s.png",Utils.getDateCurrentTimezone()),reportPng);
        javaMailSender.send(message);
        reportPng.delete();
    }

    private File getUserReport(User user) throws IOException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(user.getPassword());
        loginRequest.setEmail(user.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> requestHttpEntity = new HttpEntity<>(loginRequest,headers);
        byte[] bytes =  restTemplate.postForObject("https://venus-node-app.azurewebsites.net/api/getUserDashboardPage", requestHttpEntity, byte[].class);
        File file = new File(String.format("%s\\Reporte-%s.png",tempFolder, LocalDate.now()));
        Files.write(file.toPath(),bytes);
        return file;
    }
}
