package com.sistema.venus.controller;


import com.sistema.venus.domain.Medication;
import com.sistema.venus.services.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/rest/medicines")
public class MedicationController {

    @Autowired
    MedicationService medicationService;


    @PostMapping(value = "add")
    public ResponseEntity<Medication>createMedicine(@RequestBody Medication medicine)
        throws ValidationException{
        try{
            medicationService.saveMedicine(medicine);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping(value = "mod/{id}")
    public ResponseEntity<Medication> modificarMedicina(@RequestBody Medication med, @PathVariable Integer id){
        try{
            return ResponseEntity.ok(medicationService.modificarMedicina(med, id));
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "get")
    public ResponseEntity<List<Medication>>getMedicines() {
        try {
        List<Medication> medications = medicationService.getAllFiltered();
//        List<Medication> medications = medicationService.getMedicationByUser();
        return ResponseEntity.ok(medications);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @DeleteMapping(value = "delete/{id}")
    public ResponseEntity<Map<String, String>> deleteMedicine(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            medicationService.deleteMedicine(id);
            response.put("response", "Medication with ID " + id + " deleted successfully.");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
