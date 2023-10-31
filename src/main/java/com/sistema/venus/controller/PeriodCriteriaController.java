package com.sistema.venus.controller;

import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.servicios.PeriodCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/rest/period-criteria")
public class PeriodCriteriaController {
    @Autowired
    private PeriodCriteriaService periodCriteriaService;
    @PostMapping(value = "create")
    public ResponseEntity<PeriodCriteria> createPeriodCriteria(@RequestBody PeriodCriteria periodCriteria){
        try{
            return ResponseEntity.of(Optional.of(periodCriteriaService.createPeriodCriteria(periodCriteria)));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
