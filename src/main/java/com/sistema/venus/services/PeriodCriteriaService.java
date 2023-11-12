package com.sistema.venus.services;

import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.PeriodCriteriaRepository;
import com.sistema.venus.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PeriodCriteriaService {
    @Autowired
    private PeriodCriteriaRepository periodCriteriaRepository;
    @Autowired
    private UserRepository userRepository;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PeriodCriteria savePeriodCriteria(PeriodCriteria periodCriteria) {
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (periodCriteria.getDate() == null) {
            ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
            ZoneId zId = ZoneId.of("US/Central");
            periodCriteria.setDate(LocalDateTime.ofInstant(zdt.toInstant(), zId).toLocalDate());
        }
        PeriodCriteria existingPeriodCriteria = periodCriteriaRepository.getPeriodCriteriaByDateAndFieldName(
                periodCriteria.getDate(), periodCriteria.getFieldName(), user.getUser_id());
        if (existingPeriodCriteria != null) {
            existingPeriodCriteria.setValue(periodCriteria.getValue());
            return periodCriteriaRepository.save(existingPeriodCriteria);
        }
        periodCriteria.setUserId(user);
        return periodCriteriaRepository.save(periodCriteria);
    }

    public List<PeriodCriteria> getPeriodCriteriaByDate(String localDate) {
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return periodCriteriaRepository.getPeriodCriteriaByDate(LocalDate.parse(localDate, dateTimeFormatter),
                user.getUser_id());
    }

    public List<PeriodCriteria> getPeriodCriteriaByUser() {
        User user = userRepository.findUserByEmail((SecurityContextHolder.getContext().getAuthentication().getName()));

        return periodCriteriaRepository.getPeriodCriteriaByUserId(Long.parseLong(user.getUser_id().toString()));
    }

    /** Validación para ingresar datos relacionados al ciclo menstrual */
    public String isInputPeriodCycleValid(String periodCycleValue, LocalDate periodDate) {
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        PeriodCriteria periodCriteria = periodCriteriaRepository
                .getLastEntryOfPeriodCriteriaByUserIdAndFieldName("periodCycle", user.getUser_id());
        String message = "";
        if (periodCriteria != null && periodCycleValue != "NA") {
            if (periodDate.isBefore(periodCriteria.getDate())) {
                message = "No fue fue posible guardar los datos. La fecha no es válida. Hay ciclos posteriores a esa fecha. ";
            }
            if (periodCriteria.getValue().equals("inicio") && periodCycleValue.equals(periodCriteria.getValue())) {
                message += "No fue fue posible guardar los datos. No hay registro de finalización del ciclo menstrual anterior. ";
            }
            if (periodCriteria.getValue().equals("fin") && periodCycleValue.equals(periodCriteria.getValue())) {
                message += "No fue fue posible guardar los datos. No hay registro de inicio del ciclo menstrual. ";
            }
        }
        if (message.equals("")) {
            message += "success";
        }
        return message;
    }

    public int calculatePeriodAverage(){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLast6PeriodCycles(user.getUser_id());
        int cyclesCount = 0;
        LocalDate endCycle = null;
        LocalDate startCycle = null;
        int totalDays6PeriodCycles = 0;
        int periodAverage = 0;
        for(int i = 0; i< periodCriteria.size(); i++){
            if(i==0 && periodCriteria.get(i).getValue().equals("fin")){
                cyclesCount = cyclesCount+1;
                endCycle = periodCriteria.get(i).getDate();
            }
            if(i!=0 && periodCriteria.get(i).getValue().equals("inicio")) {
                startCycle = periodCriteria.get(i).getDate();
                totalDays6PeriodCycles = totalDays6PeriodCycles + (int) ChronoUnit.DAYS.between(startCycle, endCycle);
                endCycle = null;
                startCycle = null;
            }
            if(i!=0 && periodCriteria.get(i).getValue().equals("fin")) {
                cyclesCount = cyclesCount+1;
                endCycle = periodCriteria.get(i).getDate();
            }
        }
        if(cyclesCount>0){
            periodAverage = totalDays6PeriodCycles/cyclesCount;
        }
        return periodAverage;
    }

    public LocalDate calculateDateNextPeriod(){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLast6PeriodCycles(user.getUser_id());
        LocalDate nextPeriodStart = null;
        LocalDate periodStart_1 = null;
        LocalDate periodStart_2 = null;
        int totalDays6PeriodCycles = 0;
        int daysAveragePeriod = 0;
        List<LocalDate> startsCyclesList = new ArrayList<>();
        for(int i = 0; i< periodCriteria.size(); i++){
            if(periodCriteria.get(i).getValue().equals("inicio")){
                startsCyclesList.add(periodCriteria.get(i).getDate());
            }
        }
        if(startsCyclesList.size()>1){
            for(int i = 1; i< startsCyclesList.size(); i++){
                periodStart_2 = startsCyclesList.get(i);
                periodStart_1 = startsCyclesList.get(i-1);
                totalDays6PeriodCycles = totalDays6PeriodCycles + (int) ChronoUnit.DAYS.between(periodStart_2, periodStart_1);
            }
            daysAveragePeriod = totalDays6PeriodCycles/(startsCyclesList.size()-1);
            nextPeriodStart = startsCyclesList.get(0).plusDays(daysAveragePeriod);
        }
        return nextPeriodStart;
    }

    public int calculateAverageVariationCycle(){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLast6PeriodCycles(user.getUser_id());
        LocalDate nextPeriodStart = null;
        LocalDate periodStart_1 = null;
        LocalDate periodStart_2 = null;
        int daysAverageVariationCycle = 0;
        List<LocalDate> startsCyclesList = new ArrayList<>();
        List<Integer> cycleDuration = new ArrayList<>();
        for(int i = 0; i< periodCriteria.size(); i++){
            if(periodCriteria.get(i).getValue().equals("inicio")){
                startsCyclesList.add(periodCriteria.get(i).getDate());
            }
        }
        if(startsCyclesList.size()>1){
            for(int i = 1; i< startsCyclesList.size(); i++){
                periodStart_2 = startsCyclesList.get(i);
                periodStart_1 = startsCyclesList.get(i-1);
                cycleDuration.add((int) ChronoUnit.DAYS.between(periodStart_2, periodStart_1));
            }
            for(int i = 1; i< cycleDuration.size(); i++){
                daysAverageVariationCycle = daysAverageVariationCycle + Math.abs(cycleDuration.get(i)-cycleDuration.get(i-1));
            }
            daysAverageVariationCycle = daysAverageVariationCycle/(cycleDuration.size()-1);
        }
        return daysAverageVariationCycle;
    }

    public List<PeriodCriteria> getAllPeriodCriteriaByUserIdAndCurrentMonth() {
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        LocalDate firstDayOfMonth = YearMonth.now().atDay(1);
        LocalDate lastDayOfMonth = YearMonth.now().atEndOfMonth();
        return periodCriteriaRepository.findByUserIdAndDateBetween(user.getUser_id(), firstDayOfMonth, lastDayOfMonth);
    }
}

