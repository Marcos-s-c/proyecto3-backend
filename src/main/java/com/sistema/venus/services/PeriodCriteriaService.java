package com.sistema.venus.services;

import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.PeriodCriteriaRepository;
import com.sistema.venus.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PeriodCriteriaService {
    @Autowired
    private PeriodCriteriaRepository periodCriteriaRepository;
    @Autowired
    private UserRepository userRepository;

    public PeriodCriteria createPeriodCriteria(PeriodCriteria periodCriteria){
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        periodCriteria.setDate(LocalDate.now());
        periodCriteria.setUserId(user);
        return periodCriteriaRepository.save(periodCriteria);
    }

}
