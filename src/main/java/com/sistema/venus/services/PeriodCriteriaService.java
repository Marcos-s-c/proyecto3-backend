package com.sistema.venus.services;

import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.repo.PeriodCriteriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PeriodCriteriaService {
    @Autowired
    private PeriodCriteriaRepository periodCriteriaRepository;

    public PeriodCriteria createPeriodCriteria(PeriodCriteria periodCriteria){
        periodCriteria.setDate(LocalDate.now());
        return periodCriteriaRepository.save(periodCriteria);
    }

}
