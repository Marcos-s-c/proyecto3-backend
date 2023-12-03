package com.sistema.venus.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sistema.venus.domain.User;
import com.sistema.venus.domain.UserPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private UserPreferenceService userPreferencesService;

    @Autowired
    private UserService userService;

    @Autowired
    PeriodCriteriaService periodCriteriaService;

    @Autowired
    WhatsAppService whatsAppService;
    private ArrayList<UserPreferences> userPreferencesList;
//    public static String currentUserEmail;

    private void getAllUsersPreferences(){
        userPreferencesList = (ArrayList<UserPreferences>) userPreferencesService.getAllUserPreferences();
    }

   // @Scheduled(fixedRateString = "P1D")  // 1 dia
    @Scheduled(fixedRateString = "PT40S", initialDelay = 5000)// diez segundos
//   @Scheduled(cron = "0 0 8 * * *") // cron job todos los dias 8am
    private void printLoggedEmail(){
        LocalDate hoy = LocalDate.now();
        getAllUsersPreferences();

        userPreferencesList.stream().map(userPreference -> {
            User user = userService.findUserByEmail(userPreference.getEmailId());
            Integer daysBeforeNotice = userPreference.getAnticipation_notice();
            LocalDate dateNextPeriod = null;
            if(periodCriteriaService.calculateDateNextPeriodByEmail(user) == null){
                dateNextPeriod = hoy.plusDays(4);
            }else{
                dateNextPeriod = periodCriteriaService.calculateDateNextPeriodByEmail(user);
            }

            System.out.println("dateNextPeriod " + dateNextPeriod);
            try{
                if(dateNextPeriod.minusDays(daysBeforeNotice).equals(hoy)){
                    handleNotificaction(user, userPreference);
                }else{
                    System.out.println("dias notificacion no match " + userPreference.getEmailId());
                }
            }catch(Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            return userPreference;
        }).collect(Collectors.toList());
    }

    private void handleNotificaction(User pUser, UserPreferences pUserPreference) throws JsonProcessingException {
        Integer sendEmail = Integer.parseInt(pUserPreference.getEmail());
        Integer sendSms = Integer.parseInt(pUserPreference.getSms());
        Integer sendWapp = Integer.parseInt(pUserPreference.getWapp());
        System.out.println("\n user " + pUser.getEmail());
        if(sendEmail == 1){
            System.out.println("enviar correo ");
        }else if(sendEmail == 0){
            System.out.println("no se envia correo ");
        }

        if(sendSms == 1){
            System.out.println("enviar sendSms ");
        }else if(sendSms == 0){
            System.out.println("no se envia sendSms ");
        }

        if(sendWapp == 1){
            System.out.println("enviar sendWapp ");
            whatsAppService.sendNextPeriodMessageByEmail(pUser);
        }else if(sendWapp == 0){
            System.out.println("no se envia sendWapp ");
        }
    }

}
