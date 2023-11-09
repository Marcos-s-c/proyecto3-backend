package com.sistema.venus.controller;

import com.sistema.venus.domain.PeriodCriteria;
import com.sistema.venus.services.PeriodCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
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
    public ResponseEntity<Object> createPeriodCriteria(@RequestBody List<PeriodCriteria> periodCriteria) throws ValidationException {
        try{
            String periodCycleValue = null;
            LocalDate periodDate = null;
            for(int i= 0; i< periodCriteria.size(); i++){
                if (periodCriteria.get(i).getFieldName().equals("periodCycle")){
                    periodCycleValue =periodCriteria.get(i).getValue();
                    periodDate = periodCriteria.get(i).getDate();
                }
            }
            if (periodCycleValue != null) {
                String result = periodCriteriaService.isInputPeriodCycleValid(periodCycleValue, periodDate);
                if(!result.equals("success")) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("Success", false);
                    map.put("Message", result);
                    return ResponseEntity.badRequest().body(map);
                }
            }

            periodCriteria.forEach(criteria -> periodCriteriaService.savePeriodCriteria(criteria));
            Map<String,Object> map = new HashMap<>();
            map.put("Success",true);
            map.put("Message", "Sus datos fueron guardados exitosamente.");
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
