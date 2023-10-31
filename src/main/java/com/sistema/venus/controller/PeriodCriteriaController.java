package com.sistema.venus.controller;

import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.services.PeriodCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/period-criteria")
public class PeriodCriteriaController {
    @Autowired
    private PeriodCriteriaService periodCriteriaService;
    @PostMapping(value = "create")
    public ResponseEntity<String> createPeriodCriteria(@RequestBody List<PeriodCriteria> periodCriteria){
        try{
            periodCriteria.forEach(criteria -> periodCriteriaService.createPeriodCriteria(criteria));
            return ResponseEntity.of(Optional.of("Success"));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
