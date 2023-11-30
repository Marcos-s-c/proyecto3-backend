package com.sistema.venus.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.ApiKey;
import com.infobip.BaseUrl;
import com.infobip.api.SmsApi;
import com.infobip.model.SmsAdvancedTextualRequest;
import com.infobip.model.SmsDestination;
import com.infobip.model.SmsTextualMessage;
import com.sistema.venus.domain.UserPreferences;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;

@Service
public class SMSService {
    private static final String BASE_URL = "https://n8x8zy.api.infobip.com";
    private static final String API_KEY = "9aa8e6c30d76a96773e8311da233d489-962d5ef0-ff5e-4c6a-a5da-b15f1eecf042";

    public void sendMessage(String phone, String messageContent) throws ApiException {
        try{
             ApiClient apiClient = ApiClient.forApiKey(ApiKey.from(API_KEY))
                    .withBaseUrl(BaseUrl.from(BASE_URL))
                    .build();
            SmsApi sendSmsApi = new SmsApi(apiClient);

            SmsTextualMessage message = new SmsTextualMessage()
                    .addDestinationsItem(new SmsDestination().to(String.format("506%s",phone)))
                    .text(messageContent);

            SmsAdvancedTextualRequest smsMessageRequest = new SmsAdvancedTextualRequest()
                    .messages(Collections.singletonList(message));

            sendSmsApi.sendSmsMessage(smsMessageRequest).execute();
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}