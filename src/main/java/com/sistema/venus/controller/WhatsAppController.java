package com.sistema.venus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sistema.venus.domain.UserPreferences;
import com.sistema.venus.services.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/rest/wa/")
public class WhatsAppController {

    @Autowired
    WhatsAppService whatsAppService;
    @PostMapping("sendMessage/nextPeriod")
    public ResponseEntity<Object> sendMessageNextPeriod() throws JsonProcessingException {
        try {
            String result = whatsAppService.sendNextPeriodMessage();
            Map<String, Object> map = new HashMap<>();
            map.put("result", result);
            return ResponseEntity.ok().body(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @PostMapping("sendMessage/nextFertileDays")
    public ResponseEntity<Object> sendMessageNextFertileDays() throws JsonProcessingException {
        try {
            String result = whatsAppService.sendNextFertileDaysMessage();
            Map<String, Object> map = new HashMap<>();
            map.put("result", result);
            return ResponseEntity.ok().body(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
