package com.sistema.venus.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sistema.venus.clients.whatsapp.WhatsAppClient;
import com.sistema.venus.domain.User;
import com.sistema.venus.domain.UserPreferences;
import com.sistema.venus.repo.NotificacionesRepo;
import com.sistema.venus.repo.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class WhatsAppService {
    @Autowired
    private PeriodCriteriaService periodCriteriaService;
    @Autowired
    private UserService userService;

    @Autowired
    private NotificacionesRepo notificationRepository;
    @Autowired
    private WhatsAppClient client;

    public String sendNextPeriodMessage() throws JsonProcessingException {
        String userPhoneNumber = userService.getLoggedUser().getPhone();
        LocalDate userNextPeriod = periodCriteriaService.calculateDateNextPeriod();
        UserPreferences userPreferences = notificationRepository.getPreferenciaNotificacionByEmail(userService.getLoggedUser().getEmail());
        String WAPreference = userPreferences.getWapp();
        String message = null;
        if(WAPreference.equals("1")) {
            if (userNextPeriod != null && userNextPeriod.isAfter(LocalDate.now())) {
                message = "success";
                client.sendWAMessage(userPhoneNumber, userNextPeriod.toString(), "next_period");
            }else{
                message="success";
                client.sendWAMessage(userPhoneNumber, "no disponible", "next_period");
            }
        }
        if(WAPreference.equals("0")){
            message = "noWAPreferenceOn";
        }
        return message;
    }

    public String sendNextFertileDaysMessage() throws JsonProcessingException {
        String userPhoneNumber = userService.getLoggedUser().getPhone();
        List<LocalDate> userNextFertileDays = periodCriteriaService.calculateNextFertileDate();
        UserPreferences userPreferences = notificationRepository.getPreferenciaNotificacionByEmail(userService.getLoggedUser().getEmail());
        String WAPreference = userPreferences.getWapp();
        String message = null;
        if(WAPreference.equals("1")) {
            if (userNextFertileDays.size() > 1  && userNextFertileDays.get(1).isAfter(LocalDate.now())) {
                LocalDate date1 = userNextFertileDays.get(0);
                LocalDate date2 = userNextFertileDays.get(1);
                message = "success";
                client.sendWAMessage(userPhoneNumber, date1 + " - " + date2, "next_fertile_days");
            }else{
                message="success";
                client.sendWAMessage(userPhoneNumber, "no disponible", "next_fertile_days");
            }
        }
        if(WAPreference.equals("0")){
            message = "noWAPreferenceOn";
        }
        return message;
    }
}
