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
import java.util.List;

@Service
public class PeriodCriteriaService {
    @Autowired
    private PeriodCriteriaRepository periodCriteriaRepository;
    @Autowired
    private UserRepository userRepository;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PeriodCriteria savePeriodCriteria(PeriodCriteria periodCriteria){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(periodCriteria.getDate()==null){
            ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
            ZoneId zId = ZoneId.of("US/Central");
            periodCriteria.setDate(LocalDateTime.ofInstant(zdt.toInstant(), zId).toLocalDate());
        }
        PeriodCriteria existingPeriodCriteria = periodCriteriaRepository.getPeriodCriteriaByDateAndFieldName(periodCriteria.getDate(),periodCriteria.getFieldName(),user.getUser_id());
        if(existingPeriodCriteria!=null){
            existingPeriodCriteria.setValue(periodCriteria.getValue());
            return periodCriteriaRepository.save(existingPeriodCriteria);
        }
        periodCriteria.setUserId(user);
        return periodCriteriaRepository.save(periodCriteria);
    }

    public List<PeriodCriteria> getPeriodCriteriaByDate(String localDate){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return periodCriteriaRepository.getPeriodCriteriaByDate(LocalDate.parse(localDate,dateTimeFormatter),user.getUser_id());
    }
}
