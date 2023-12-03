package com.sistema.venus.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infobip.ApiException;
import com.sistema.venus.domain.User;
import com.sistema.venus.domain.UserPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Autowired
    SMSService smsService;

    @Autowired
    AuthService authService;
    private ArrayList<UserPreferences> userPreferencesList;

    private void getAllUsersPreferences(){
        userPreferencesList = (ArrayList<UserPreferences>) userPreferencesService.getAllUserPreferences();
    }

   // @Scheduled(fixedRateString = "P1D")  // 1 dia
//    @Scheduled(fixedRateString = "PT40S", initialDelay = 5000)// diez segundos
   @Scheduled(cron = "0 0 8 * * *") // cron job todos los dias 8am
    private void handleNotificeSchedule(){
        LocalDate hoy = LocalDate.now();
        getAllUsersPreferences();

        userPreferencesList.stream().map(userPreference -> {
            User user = userService.findUserByEmail(userPreference.getEmailId());
            Integer daysBeforeNotice = userPreference.getAnticipation_notice();
            LocalDate dateNextPeriod = null;
            List<LocalDate> userNextFertileDays = periodCriteriaService.calculateNextFertileDateByEMailId(user);
            if(periodCriteriaService.calculateDateNextPeriodByEmail(user) == null){
                dateNextPeriod = hoy.plusDays(4);
            }else{
                dateNextPeriod = periodCriteriaService.calculateDateNextPeriodByEmail(user);
            }

            System.out.println("dateNextPeriod " + dateNextPeriod);
            try{
                if(dateNextPeriod.minusDays(daysBeforeNotice).equals(hoy)){
                    handlePeriodNotificaction(user, userPreference, dateNextPeriod);
                    handleFertileNotice(user, userPreference, userNextFertileDays);
                }else{
                    System.out.println("dias notificacion no concuerdan " + userPreference.getEmailId());
                }
            }catch(Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return userPreference;
        }).collect(Collectors.toList());
    }

    private void handlePeriodNotificaction(User pUser, UserPreferences pUserPreference, LocalDate nextPeriod) throws JsonProcessingException, ApiException {
        String smsMessage = "Venus informa:\n Su próximo periodo se pronostica para el: " + nextPeriod;
        Integer sendEmail = Integer.parseInt(pUserPreference.getEmail());
        Integer sendSms = Integer.parseInt(pUserPreference.getSms());
        Integer sendWapp = Integer.parseInt(pUserPreference.getWapp());
        if(sendEmail == 1) authService.sendEmailNotice(smsMessage, pUser);
        if(sendSms == 1) smsService.sendMessage(pUser.getPhone(), smsMessage);
        if(sendWapp == 1) whatsAppService.sendNextPeriodMessageByEmailId(pUser);
    }

    private void handleFertileNotice(User pUser, UserPreferences pUserPreference, List<LocalDate> userNextFertileDays) throws JsonProcessingException, ApiException {
        Integer sendEmail = Integer.parseInt(pUserPreference.getEmail());
        Integer sendSms = Integer.parseInt(pUserPreference.getSms());
        Integer sendWapp = Integer.parseInt(pUserPreference.getWapp());
        LocalDate date1 = userNextFertileDays.get(0);
        LocalDate date2 = userNextFertileDays.get(1);
        String smsMessage = "Venus informa:\n Sus próximos dias fértiles se pronostican entre las siguientes fechas:\n " +"Comienza: "+ date1 + "\n Termina: " + date2;
        if(sendEmail == 1){
            if (userNextFertileDays.size() > 1  && userNextFertileDays.get(1).isAfter(LocalDate.now())) {
                authService.sendEmailNotice(smsMessage, pUser);
            }else{
                smsMessage = "Venus informa:\n Próximos dias fértiles no dispinibles";
                authService.sendEmailNotice(smsMessage, pUser);
            }
        }
        if(sendSms == 1) smsService.sendMessage(pUser.getPhone(), smsMessage);
        if(sendWapp == 1) whatsAppService.sendNextFertileDaysMessageByEMailId(pUser);
    }

}
