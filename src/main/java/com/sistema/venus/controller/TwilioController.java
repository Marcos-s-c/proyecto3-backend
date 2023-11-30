package com.sistema.venus.controller;

import com.infobip.ApiException;
import com.sistema.venus.domain.UserPreferences;
import com.sistema.venus.repo.NotificacionesRepo;
import com.sistema.venus.services.PeriodCriteriaService;
import com.sistema.venus.services.SMSService;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/rest/twilio/")
public class TwilioController {

    @Autowired
    private SMSService SMSService;
    @Autowired
    private UserService userService;
    @Autowired
    private PeriodCriteriaService periodCriteriaService;
    @Autowired
    private NotificacionesRepo notificationRepository;
    @PostMapping("sendMessage/nextPeriod")
    public ResponseEntity<Object> sendMessageNextPeriod() throws ApiException {
        try {
            String userPhoneNumber = userService.getLoggedUser().getPhone();
            LocalDate userNextPeriod = periodCriteriaService.calculateDateNextPeriod();
            UserPreferences userPreferences = notificationRepository.getPreferenciaNotificacionByEmail(userService.getLoggedUser().getEmail());
            String smsPreference = userPreferences.getSms();
            String message = null;
            if(smsPreference.equals("1")){
                if (userNextPeriod != null  && userNextPeriod.isAfter(LocalDate.now()) ) {
                    message = "Venus informa: Su próximo periodo se pronostica para el: " + userNextPeriod;
                }else{
                    message = "Venus informa: No hay datos pronóstico repecto a su próxima menstruación.";
                }
                SMSService.sendMessage(userPhoneNumber, message);
            }
            if(smsPreference.equals("0")){
                message = "Debe ajustar sus preferencias de notificaciones, para recibir mensajes de texto.";
            }
            Map<String, Object> map = new HashMap<>();
            map.put("result", message);
            return ResponseEntity.ok().body(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("sendMessage/nextFertileDay")
    public ResponseEntity<Object> nextFertileDay() throws ApiException {
        try {
            String userPhoneNumber = userService.getLoggedUser().getPhone();
            List<LocalDate> userNextFertileDays = periodCriteriaService.calculateNextFertileDate();
            UserPreferences userPreferences = notificationRepository.getPreferenciaNotificacionByEmail(userService.getLoggedUser().getEmail());
            String smsPreference = userPreferences.getSms();
            String message = null;
            if(smsPreference.equals("1")){
                if (userNextFertileDays.size() > 1  && userNextFertileDays.get(1).isAfter(LocalDate.now())) {
                    LocalDate date1 = userNextFertileDays.get(0);
                    LocalDate date2 = userNextFertileDays.get(1);
                    message = String.format("Venus informa: Sus próximos días fértiles se pronostican para ser los siguientes: %s - %s",date1,date2);
                }else{
                    message = "Venus informa: No hay datos pronóstico repecto a sus próximos días fértiles";
                }
                SMSService.sendMessage(userPhoneNumber, message);
            }
            if(smsPreference.equals("0")){
                message = "Debe ajustar sus preferencias de notificaciones, para recibir mensajes de texto.";
            }
            Map<String, Object> map = new HashMap<>();
            map.put("result", message);
            return ResponseEntity.ok().body(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
