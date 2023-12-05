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
    @Autowired
    private NotificationService notificationsService;

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
            notificationsService.createPeriodCriteriaNotification(periodCriteria);
            return periodCriteriaRepository.save(existingPeriodCriteria);
        }
        periodCriteria.setUserId(user);
        notificationsService.createPeriodCriteriaNotification(periodCriteria);
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

    /** Validation for periodCriteria data*/
    public String isInputPeriodCycleValid(String periodCycleValueParameter, LocalDate periodDateParameter) {
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        PeriodCriteria periodCriteriaLastEntry = periodCriteriaRepository
                .getLastEntryOfPeriodCriteriaByUserIdAndFieldName("periodCycle", user.getUser_id());
        String message = "";


        if(periodCriteriaLastEntry != null && periodCriteriaLastEntry.getDate().equals(periodDateParameter) ){
            if(!periodCriteriaLastEntry.getValue().equals(periodCycleValueParameter) && !periodCycleValueParameter.equals("NA")){
                message = "No fue posible cambiar el dato porque genera inconsistencias.";
            }
        }else{
            if (periodCriteriaLastEntry != null && !periodCycleValueParameter.equals("NA")) {
                /**Saving "Inicio" or "Fin" is not allow before last entry date of "Inicio" or "Fin"**/
                if (periodDateParameter.isBefore(periodCriteriaLastEntry.getDate())) {
                    message = "No fue posible guardar los datos. La fecha no es válida. Hay ciclos posteriores a esa fecha. ";
                }
                /**Saving **/
                if (periodCriteriaLastEntry.getValue().equals("Inicio") && periodCycleValueParameter.equals(periodCriteriaLastEntry.getValue())) {
                    message += "No fue posible guardar los datos. No hay registro de finalización del ciclo menstrual anterior. ";
                }
                if (periodCriteriaLastEntry.getValue().equals("Fin") && periodCycleValueParameter.equals(periodCriteriaLastEntry.getValue())) {
                    message += "No fue posible guardar los datos. No hay registro de inicio del ciclo menstrual. ";
                }
            }
        }

        PeriodCriteria periodCycleByDateAndUser = periodCriteriaRepository.getPeriodCycleByDateAndUser(user.getUser_id(), periodDateParameter);
        if(periodCycleValueParameter.equals("NA") && periodCycleByDateAndUser != null &&
                (periodCycleByDateAndUser.getValue().equals("Inicio") || periodCycleByDateAndUser.getValue().equals("Fin"))
                && !periodCriteriaLastEntry.getDate().equals(periodCycleByDateAndUser.getDate())){
            message += "No fue fue posible guardar los datos, porque la solicitud puede generar inconsistencias. ";
        }

        if(periodCycleByDateAndUser != null && periodCycleByDateAndUser.getDate().equals(periodDateParameter) && periodCycleByDateAndUser.getValue().equals(periodCycleValueParameter)){
            message = "success";
        }

        if (message.equals("")) {
            message = "success";
        }
        return message;
    }

    public Integer calculatePeriodAverage(){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLast6PeriodCycles(user.getUser_id());
        int cyclesCount = 0;
        LocalDate endCycle = null;
        LocalDate startCycle = null;
        int totalDays6PeriodCycles = 0;
        Integer periodAverage = null;
        for(int i = 0; i< periodCriteria.size(); i++){
            if(i==0 && periodCriteria.get(i).getValue().equals("Fin")){
                cyclesCount = cyclesCount+1;
                endCycle = periodCriteria.get(i).getDate();
            }
            if(i!=0 && periodCriteria.get(i).getValue().equals("Inicio")) {
                startCycle = periodCriteria.get(i).getDate();
                totalDays6PeriodCycles = totalDays6PeriodCycles + (int) ChronoUnit.DAYS.between(startCycle, endCycle);
                endCycle = null;
                startCycle = null;
            }
            if(i!=0 && periodCriteria.get(i).getValue().equals("Fin")) {
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
            if(periodCriteria.get(i).getValue().equals("Inicio")){
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

    public LocalDate calculateDateNextPeriodByEmail(User pUser){
        User user = pUser;
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLast6PeriodCycles(user.getUser_id());
        LocalDate nextPeriodStart = null;
        LocalDate periodStart_1 = null;
        LocalDate periodStart_2 = null;
        int totalDays6PeriodCycles = 0;
        int daysAveragePeriod = 0;
        List<LocalDate> startsCyclesList = new ArrayList<>();
        for(int i = 0; i< periodCriteria.size(); i++){
            if(periodCriteria.get(i).getValue().equals("Inicio")){
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

    public Integer calculateAverageVariationCycle(){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLast6PeriodCycles(user.getUser_id());
        LocalDate nextPeriodStart = null;
        LocalDate periodStart_1 = null;
        LocalDate periodStart_2 = null;
        Integer daysAverageVariationCycle = null;
        List<LocalDate> startsCyclesList = new ArrayList<>();
        List<Integer> cycleDuration = new ArrayList<>();
        for(int i = 0; i< periodCriteria.size(); i++){
            if(periodCriteria.get(i).getValue().equals("Inicio")){
                startsCyclesList.add(periodCriteria.get(i).getDate());
            }
        }
        if(startsCyclesList.size()>2){
            for(int i = 1; i< startsCyclesList.size(); i++){
                periodStart_2 = startsCyclesList.get(i);
                periodStart_1 = startsCyclesList.get(i-1);
                cycleDuration.add((int) ChronoUnit.DAYS.between(periodStart_2, periodStart_1));
            }
            daysAverageVariationCycle = 0;
            for(int i = 1; i< cycleDuration.size(); i++){
                daysAverageVariationCycle = daysAverageVariationCycle + Math.abs(cycleDuration.get(i)-cycleDuration.get(i-1));
            }
                daysAverageVariationCycle = daysAverageVariationCycle/(cycleDuration.size()-1);
        }
        return daysAverageVariationCycle;
    }

    public Integer calculateAverageDurationCycle(){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLast6PeriodCycles(user.getUser_id());
        LocalDate nextPeriodStart = null;
        LocalDate periodStart_1 = null;
        LocalDate periodStart_2 = null;
        Integer daysAverageDurationCycle = null;
        List<LocalDate> startsCyclesList = new ArrayList<>();
        List<Integer> cycleDuration = new ArrayList<>();
        for(int i = 0; i< periodCriteria.size(); i++){
            if(periodCriteria.get(i).getValue().equals("Inicio")){
                startsCyclesList.add(periodCriteria.get(i).getDate());
            }
        }
        if(startsCyclesList.size()>2){
            for(int i = 1; i< startsCyclesList.size(); i++){
                periodStart_2 = startsCyclesList.get(i);
                periodStart_1 = startsCyclesList.get(i-1);
                cycleDuration.add((int) ChronoUnit.DAYS.between(periodStart_2, periodStart_1));
            }
            daysAverageDurationCycle = 0;
            for(int i = 0; i< cycleDuration.size(); i++){
                daysAverageDurationCycle += cycleDuration.get(i) ;
            }
            daysAverageDurationCycle = daysAverageDurationCycle/(cycleDuration.size());
        }
        return daysAverageDurationCycle;
    }

    public Integer calculateAverageDurationCycleByEMailId(User user){
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLast6PeriodCycles(user.getUser_id());
        LocalDate nextPeriodStart = null;
        LocalDate periodStart_1 = null;
        LocalDate periodStart_2 = null;
        Integer daysAverageDurationCycle = null;
        List<LocalDate> startsCyclesList = new ArrayList<>();
        List<Integer> cycleDuration = new ArrayList<>();
        for(int i = 0; i< periodCriteria.size(); i++){
            if(periodCriteria.get(i).getValue().equals("Inicio")){
                startsCyclesList.add(periodCriteria.get(i).getDate());
            }
        }
        if(startsCyclesList.size()>2){
            for(int i = 1; i< startsCyclesList.size(); i++){
                periodStart_2 = startsCyclesList.get(i);
                periodStart_1 = startsCyclesList.get(i-1);
                cycleDuration.add((int) ChronoUnit.DAYS.between(periodStart_2, periodStart_1));
            }
            daysAverageDurationCycle = 0;
            for(int i = 0; i< cycleDuration.size(); i++){
                daysAverageDurationCycle += cycleDuration.get(i) ;
            }
            daysAverageDurationCycle = daysAverageDurationCycle/(cycleDuration.size());
        }
        return daysAverageDurationCycle;
    }
    public List<PeriodCriteria> getAllPeriodCriteriaByUserIdAndCurrentMonth() {
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        LocalDate firstDayOfMonth = YearMonth.now().atDay(1);
        LocalDate lastDayOfMonth = YearMonth.now().atEndOfMonth();
        return periodCriteriaRepository.findByUserIdAndDateBetween(user.getUser_id(), firstDayOfMonth, lastDayOfMonth);
    }
   
        public List<LocalDate> calculateNextFertileDate(){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLastPeriodCycle(user.getUser_id());
        LocalDate startCycle = null;
        LocalDate endCycle = null;
        List<PeriodCriteria> temperatureList;
        List<PeriodCriteria> cervicalFluidList;
        LocalDate ovulationDay = null;
        LocalDate startFertileDay = null;
        List<LocalDate> fertileRangeDays = new ArrayList<>();

        for(int i = 0; i< periodCriteria.size(); i++) {
            if(i==0 && periodCriteria.get(i).getValue().equals("Inicio")){
                startCycle = periodCriteria.get(i).getDate();
            }
            if(startCycle != null && endCycle == null && periodCriteria.get(i).getValue().equals("Fin")){
                endCycle = periodCriteria.get(i).getDate();
            }
            if(startCycle == null && periodCriteria.get(i).getValue().equals("Inicio")){
                startCycle = periodCriteria.get(i).getDate();
            }
        }

        if(startCycle != null && endCycle != null){
            temperatureList = periodCriteriaRepository.getPeriodCriteriaLastPeriodCycle("temperature", user.getUser_id(), startCycle, endCycle);
            cervicalFluidList = periodCriteriaRepository.getPeriodCriteriaLastPeriodCycle("fluidAmount", user.getUser_id(), startCycle, endCycle);

            for(int i = 1; i< temperatureList.size(); i++) {
                if((Double.parseDouble(temperatureList.get(i).getValue()) - Double.parseDouble(temperatureList.get(i-1).getValue()) ) > 0.7){
                    ovulationDay = temperatureList.get(i).getDate();
                    break;
                }
            }

            for(int i = 0; i< cervicalFluidList.size(); i++){
                if(cervicalFluidList.get(i).getValue().equals("Muy húmedo") || cervicalFluidList.get(i).getValue().equals("Húmedo")){
                    startFertileDay = cervicalFluidList.get(i).getDate();
                    break;
                }
            }
        }

        Integer averageDurationCycle = calculateAverageDurationCycle();

        if (averageDurationCycle != null) {
            if(ovulationDay != null && startFertileDay != null){
                fertileRangeDays.add(startFertileDay.plusDays(averageDurationCycle));
                fertileRangeDays.add(ovulationDay.plusDays(1 + averageDurationCycle));
            }
            if(ovulationDay == null && startFertileDay != null){
                fertileRangeDays.add(startFertileDay.plusDays(calculateAverageDurationCycle()));
                fertileRangeDays.add(startFertileDay.plusDays(9 + averageDurationCycle));
            }
            if(ovulationDay != null && startFertileDay == null){
                fertileRangeDays.add(ovulationDay.minusDays(8).plusDays(averageDurationCycle));
                fertileRangeDays.add(ovulationDay.plusDays(1).plusDays(averageDurationCycle));
            }
        }

        return fertileRangeDays; 
    }

    public List<LocalDate> calculateNextFertileDateByEMailId(User user){
        List<PeriodCriteria> periodCriteria = periodCriteriaRepository.getLastPeriodCycle(user.getUser_id());
        LocalDate startCycle = null;
        LocalDate endCycle = null;
        List<PeriodCriteria> temperatureList;
        List<PeriodCriteria> cervicalFluidList;
        LocalDate ovulationDay = null;
        LocalDate startFertileDay = null;
        List<LocalDate> fertileRangeDays = new ArrayList<>();

        for(int i = 0; i< periodCriteria.size(); i++) {
            if(i==0 && periodCriteria.get(i).getValue().equals("Inicio")){
                startCycle = periodCriteria.get(i).getDate();
            }
            if(startCycle != null && endCycle == null && periodCriteria.get(i).getValue().equals("Fin")){
                endCycle = periodCriteria.get(i).getDate();
            }
            if(startCycle == null && periodCriteria.get(i).getValue().equals("Inicio")){
                startCycle = periodCriteria.get(i).getDate();
            }
        }

        if(startCycle != null && endCycle != null){
            temperatureList = periodCriteriaRepository.getPeriodCriteriaLastPeriodCycle("temperature", user.getUser_id(), startCycle, endCycle);
            cervicalFluidList = periodCriteriaRepository.getPeriodCriteriaLastPeriodCycle("fluidAmount", user.getUser_id(), startCycle, endCycle);

            for(int i = 1; i< temperatureList.size(); i++) {
                if((Double.parseDouble(temperatureList.get(i).getValue()) - Double.parseDouble(temperatureList.get(i-1).getValue()) ) > 0.7){
                    ovulationDay = temperatureList.get(i).getDate();
                    break;
                }
            }

            for(int i = 0; i< cervicalFluidList.size(); i++){
                if(cervicalFluidList.get(i).getValue().equals("Muy húmedo") || cervicalFluidList.get(i).getValue().equals("Húmedo")){
                    startFertileDay = cervicalFluidList.get(i).getDate();
                    break;
                }
            }
        }

        Integer averageDurationCycle = calculateAverageDurationCycleByEMailId(user);

        if (averageDurationCycle != null) {
            if(ovulationDay != null && startFertileDay != null){
                fertileRangeDays.add(startFertileDay.plusDays(averageDurationCycle));
                fertileRangeDays.add(ovulationDay.plusDays(1 + averageDurationCycle));
            }
            if(ovulationDay == null && startFertileDay != null){
                fertileRangeDays.add(startFertileDay.plusDays(calculateAverageDurationCycleByEMailId(user)));
                fertileRangeDays.add(startFertileDay.plusDays(9 + averageDurationCycle));
            }
            if(ovulationDay != null && startFertileDay == null){
                fertileRangeDays.add(ovulationDay.minusDays(8).plusDays(averageDurationCycle));
                fertileRangeDays.add(ovulationDay.plusDays(1).plusDays(averageDurationCycle));
            }
        }

        return fertileRangeDays;
    }
}

