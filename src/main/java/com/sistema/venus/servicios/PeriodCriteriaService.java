package com.sistema.venus.servicios;

import com.sistema.venus.controller.PeriodCriteriaController;
import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.repo.PeriodCriteriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeriodCriteriaService {
    @Autowired
    private PeriodCriteriaRepository periodCriteriaRepository;

    public PeriodCriteria createPeriodCriteria(PeriodCriteria periodCriteria){
        return periodCriteriaRepository.save(periodCriteria);
    }

}
