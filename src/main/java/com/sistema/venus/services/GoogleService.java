package com.sistema.venus.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.sistema.venus.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Service
public class GoogleService {

    @Autowired
    private JsonFactory jsonFactory;
    @Autowired
    private DataStoreFactory dataStoreFactory;

    public void syncCalendarEvents(User user) throws IOException, GeneralSecurityException {

        BasicAuthentication creds = new BasicAuthentication("venus49117413@gmail.com","utga reaz otcq vucf");

        // Load service account credentials from the JSON file
        FileInputStream credentialsStream = new FileInputStream(getClass().getResource("/client_secrets.json").getFile());

        // Authorize with the loaded credentials
        GoogleCredential credentials = GoogleCredential.fromStream(credentialsStream)
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        // Create a Calendar service using the authorized credentials
        Calendar service = new Calendar.Builder(
                credentials.getTransport(),
                credentials.getJsonFactory(),
                credentials)
                .setApplicationName("YourAppName")
                .build();


        Event event = new Event().setSummary("Venus").setDescription("Día fertil");
        EventDateTime eventDateTime = new EventDateTime().setDateTime(new DateTime(new Date()));
        EventDateTime eventEndTime = new EventDateTime().setDateTime(new DateTime(new Date()));
        event.setStart(eventDateTime);
        event.setEnd(eventEndTime);

        event.setAttendees(Arrays.asList(new EventAttendee().setEmail(user.getEmail())));

        service.events().insert("primary",event).execute();
    }
}
