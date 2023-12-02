package com.sistema.venus.services;

import com.sistema.venus.domain.UserPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private UserPreferenceService userPreferencesService;
    private ArrayList<UserPreferences> userPreferencesList;

    private void getAllUsersPreferences(){
        userPreferencesList = (ArrayList<UserPreferences>) userPreferencesService.getAllUserPreferences();
    }

   // @Scheduled(fixedRateString = "P1D")  // 1 dia
    @Scheduled(fixedRateString = "PT20S")// diez segundos
//   @Scheduled(cron = "0 0 8 * * *") // cron job todos los dias 8am
    private void printLoggedEmail(){
        getAllUsersPreferences();
        userPreferencesList.stream().map(item -> {
            System.out.println("item " + item);
            return item;
        }).collect(Collectors.toList());

//        for (UserPreferences p:userPreferencesList) {
//            System.out.println(p.toString());
//        }

    }

}
