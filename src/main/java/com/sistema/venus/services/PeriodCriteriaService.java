package com.sistema.venus.services;

import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.PeriodCriteriaRepository;
import com.sistema.venus.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PeriodCriteriaService {
    @Autowired
    private PeriodCriteriaRepository periodCriteriaRepository;
    @Autowired
    private UserRepository userRepository;

    public PeriodCriteria savePeriodCriteria(PeriodCriteria periodCriteria){
        if(periodCriteria.getDate()==null)periodCriteria.setDate(LocalDate.now());
        PeriodCriteria existingPeriodCriteria = periodCriteriaRepository.getPeriodCriteriaByDateAndFieldName(periodCriteria.getDate(),periodCriteria.getFieldName());
        if(existingPeriodCriteria!=null){
            existingPeriodCriteria.setValue(periodCriteria.getValue());
            return periodCriteriaRepository.save(existingPeriodCriteria);
        }
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        periodCriteria.setUserId(user);
        return periodCriteriaRepository.save(periodCriteria);
    }

}
