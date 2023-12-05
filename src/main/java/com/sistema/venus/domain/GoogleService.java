package com.sistema.venus.domain;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

@Service
public class GoogleService {

    @Autowired
    JsonFactory jsonFactory;
    public void syncCalendarEvents() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,new InputStreamReader(Files.newInputStream(Paths.get("/client_secrets.json"))));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
}
