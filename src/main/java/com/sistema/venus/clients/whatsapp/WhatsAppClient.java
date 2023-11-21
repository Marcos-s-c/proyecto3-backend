package com.sistema.venus.clients.whatsapp;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.sistema.venus.clients.whatsapp.request.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class WhatsAppClient {
    private static final String SEND_MESSAGE_WA_URL ="https://graph.facebook.com/v17.0/165060313357938/messages";

    @Value("${whatsapp.token}")
    private String whatsappToken;


    public Boolean sendWAMessage(String toNumber, String text, String templateName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ whatsappToken);
        WhatsAppMessageTemplate whatsAppMessageTemplate = getWhatsAppMessageTemplate(text, templateName);
        WhatsAppMessageRequest whatsAppMessageRequest = new WhatsAppMessageRequest("whatsapp", "+506" + toNumber, "template", whatsAppMessageTemplate);

        HttpEntity<String> requestHttpEntity = new HttpEntity<>(mapper.writeValueAsString(whatsAppMessageRequest), headers);
        String response = restTemplate.postForObject(SEND_MESSAGE_WA_URL, requestHttpEntity, String.class);

        return response != null;
    }

    private WhatsAppMessageTemplate getWhatsAppMessageTemplate(String text, String templateName) {
        WhatsAppParameters whatsAppParameters = new WhatsAppParameters("text", text);
        List<WhatsAppParameters> parameters = new ArrayList<>();
        parameters.add(whatsAppParameters);
        WhatsAppComponent whatsAppComponent = new WhatsAppComponent("body", parameters);
        List<WhatsAppComponent> components = new ArrayList<>();
        components.add(whatsAppComponent);
        WhatsAppLanguage whatsAppLanguage = new WhatsAppLanguage("es");
        WhatsAppMessageTemplate whatsAppMessageTemplate= new WhatsAppMessageTemplate(templateName, whatsAppLanguage, components);
        return whatsAppMessageTemplate;
    }

}
