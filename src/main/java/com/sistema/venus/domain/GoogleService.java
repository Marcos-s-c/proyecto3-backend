package com.sistema.venus.domain;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

@Service
public class GoogleService {

    @Autowired
    private JsonFactory jsonFactory;
    @Autowired
    private DataStoreFactory dataStoreFactory;

    public void syncCalendarEvents(User user) throws IOException, GeneralSecurityException, URISyntaxException {
        URL resource = getClass().getResource("/client_secrets.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,new InputStreamReader(Files.newInputStream(Path.of(resource.toURI()))));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential creds = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        Event event = new Event().setSummary("Venus").setDescription("Día fertil");
        EventDateTime eventDateTime = new EventDateTime().setDateTime(new DateTime(new Date()));
        EventDateTime eventEndTime = new EventDateTime().setDateTime(new DateTime(new Date()));
        event.setStart(eventDateTime);
        event.setEnd(eventEndTime);

        event.setAttendees(Arrays.asList(new EventAttendee().setEmail(user.getEmail())));

        Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, creds)
                .setApplicationName("Venus")
                .build();

        service.events().insert("primary",event).execute();
    }
}
