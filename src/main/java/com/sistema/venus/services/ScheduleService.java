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
//    @Scheduled(fixedRateString = "PT30S", initialDelay = 5000)// 30 segundos
   @Scheduled(cron = "0 0 8 * * *") // cron job todos los dias 8am
    private void handleNotificeSchedule(){
        LocalDate hoy = LocalDate.now();
        getAllUsersPreferences();

        userPreferencesList.forEach(userPreference -> {
            User user = userService.findUserByEmail(userPreference.getEmailId());
            Integer daysBeforeNotice = userPreference.getAnticipation_notice();
            LocalDate dateNextPeriod = null;
            try{
                if(periodCriteriaService.calculateDateNextPeriodByEmail(user) == null){
                    // dateNextPeriod = hoy.plusDays(10);
//                    System.out.println("siguiente periodo nulo " + userPreference.getEmailId());
                }else{
                    List<LocalDate> userNextFertileDays = periodCriteriaService.calculateNextFertileDateByEMailId(user);
                    dateNextPeriod = periodCriteriaService.calculateDateNextPeriodByEmail(user);
                    if(dateNextPeriod.minusDays(daysBeforeNotice).equals(hoy)){
//                        System.out.println("siguiente periodo valido - match de dias de notificacion" + userPreference.getEmailId());
                        handlePeriodNotificaction(user, userPreference, dateNextPeriod);
                    }
                    handleFertileNotice(user, userPreference, userNextFertileDays,daysBeforeNotice);
                }

            }catch(Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    private void handlePeriodNotificaction(User pUser, UserPreferences pUserPreference, LocalDate nextPeriod) throws JsonProcessingException, ApiException {
        String smsMessage = "Venus informa:\n Su próximo periodo se pronostica para el: " + nextPeriod;
        Integer sendEmail = Integer.parseInt(pUserPreference.getEmail());
        Integer sendSms = Integer.parseInt(pUserPreference.getSms());
        Integer sendWapp = Integer.parseInt(pUserPreference.getWapp());
//        System.out.println(pUser.getEmail());
//        if(sendEmail == 1) System.out.println("handlePeriodNotificaction enviar sendEmail - " + smsMessage);
//        if(sendSms == 1) System.out.println("handlePeriodNotificaction enviar sendSms - " + smsMessage);
//        if(sendWapp == 1) System.out.println("handlePeriodNotificaction enviar sendWapp - " + smsMessage + "\n");
        if(sendEmail == 1) authService.sendEmailNotice(smsMessage, pUser);
        if(sendSms == 1) smsService.sendMessage(pUser.getPhone(), smsMessage);
        if(sendWapp == 1) whatsAppService.sendNextPeriodMessageByEmailId(pUser);
    }

    private void handleFertileNotice(User pUser, UserPreferences pUserPreference, List<LocalDate> userNextFertileDays, Integer daysBeforeNotice) {
        Integer sendEmail = Integer.parseInt(pUserPreference.getEmail());
        Integer sendSms = Integer.parseInt(pUserPreference.getSms());
        Integer sendWapp = Integer.parseInt(pUserPreference.getWapp());
        try {
            LocalDate date1 = LocalDate.parse("1000-01-01");
            LocalDate date2 = LocalDate.parse("1000-01-01");
            if (userNextFertileDays.size() > 1) {
                date1 = userNextFertileDays.get(0);
                date2 = userNextFertileDays.get(1);
            }
            String smsMessage = "Venus informa:\n Sus próximos dias fértiles se pronostican entre las siguientes fechas:\n " +"Comienza: "+ date1 + "\n Termina: " + date2;

            if (userNextFertileDays.size() > 1  && LocalDate.now().plusDays(daysBeforeNotice).equals(userNextFertileDays.get(0))){
                if(sendEmail == 1) authService.sendEmailNotice(smsMessage, pUser);
                if(sendSms == 1) smsService.sendMessage(pUser.getPhone(), smsMessage);
                if(sendWapp == 1) whatsAppService.sendNextFertileDaysMessageByEMailId(pUser);
    //            System.out.println(pUser.getEmail());
    //            if(sendEmail == 1) System.out.println("handleFertileNotice enviar sendEmail - " + smsMessage);
    //            if(sendSms == 1) System.out.println("handleFertileNotice enviar sendSms" + smsMessage);
    //            if(sendWapp == 1) System.out.println("handleFertileNotice enviar sendWapp\n" + smsMessage + "\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
