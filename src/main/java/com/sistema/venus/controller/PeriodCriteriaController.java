package com.sistema.venus.controller;

import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.services.PeriodCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/rest/period-criteria")
public class PeriodCriteriaController {
    @Autowired
    private PeriodCriteriaService periodCriteriaService;
    @PostMapping(value = "create")
    public ResponseEntity<Object> createPeriodCriteria(@RequestBody List<PeriodCriteria> periodCriteria){
        try{
            periodCriteria.forEach(criteria -> periodCriteriaService.savePeriodCriteria(criteria));
            Map<String,Boolean> map = new HashMap<>();
            map.put("Success",true);
            return ResponseEntity.ok(map);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "getPeriodCriteriaByDate")
    public ResponseEntity<List<PeriodCriteria>> getPeriodCriteriaByDate(@RequestParam String date){
        try{
            return ResponseEntity.of(Optional.of(periodCriteriaService.getPeriodCriteriaByDate(date)));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
