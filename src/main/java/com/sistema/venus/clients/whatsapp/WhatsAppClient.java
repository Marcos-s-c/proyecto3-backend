package com.sistema.venus.clients.whatsapp;

import com.sistema.venus.clients.whatsapp.request.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class WhatsAppClient {
    private static final String SEND_MESSAGE_WA_URL ="https://graph.facebook.com/v.17.0/165060313357938/messages";

    @Value("${whatsapp.token}")
    private String whatsappToken;

    public Boolean sendWAMessage(String toNumber, String text) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ whatsappToken);
        WhatsAppMessageTemplate whatsAppMessageTemplate = getWhatsAppMessageTemplate(text);
        WhatsAppMessageRequest whatsAppMessageRequest = new WhatsAppMessageRequest("whatsapp", "+506" + toNumber, "template", whatsAppMessageTemplate);

        HttpEntity<WhatsAppMessageRequest> requestHttpEntity = new HttpEntity<>(whatsAppMessageRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(SEND_MESSAGE_WA_URL, HttpMethod.POST, requestHttpEntity, String.class);
        
        return response.getStatusCode().is2xxSuccessful();
    }

    private WhatsAppMessageTemplate getWhatsAppMessageTemplate(String text) {
        WhatsAppParameters whatsAppParameters = new WhatsAppParameters("text", text);
        List<WhatsAppParameters> parameters = new ArrayList<>();
        parameters.add(whatsAppParameters);
        WhatsAppComponent whatsAppComponent = new WhatsAppComponent("body", parameters);
        List<WhatsAppComponent> components = new ArrayList<>();
        components.add(whatsAppComponent);
        WhatsAppLanguage whatsAppLanguage = new WhatsAppLanguage("es");
        WhatsAppMessageTemplate whatsAppMessageTemplate= new WhatsAppMessageTemplate("next_period", whatsAppLanguage, components);
        return whatsAppMessageTemplate;
    }

}
