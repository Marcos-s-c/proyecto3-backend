package com.sistema.venus.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TwilioService {
    public static final String ACCOUNT_SID ="AC08414af662f9787c8f8bab3510aa304d";
    public static final String AUTH_TOKEN = "6009f3843aec26e9c15443eff47b8afb";

    @PostConstruct
    public void setup(){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendMessage(String phone, String smsMessage){
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("+506"+phone),
                        new com.twilio.type.PhoneNumber("+13343669701"), smsMessage)
                        //new com.twilio.type.PhoneNumber("+16785353163"), smsMessage)
                .create();
    }
}