package com.sistema.venus.controller;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.*;
import com.sendgrid.helpers.mail.objects.*;
import com.sistema.venus.repo.UserRepository;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping(value = "authTest")
    public ResponseEntity<String> getUser(){
        try{
            return ResponseEntity.of(Optional.of("Success"));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "/recuperaContra")
    public ResponseEntity<String> recuperarContra(@PathVariable(value = "email") String email) throws IOException {
        String result = "";
        Email from = new Email("correo_en_espera");
        String subject = "Prueba de venus";
        Email to = new Email(email);
        //Content content = new Content("text/plain", "Codigo Generado: ");
        Content content = new Content("text/plain", "Codigo Generado: " + userService.generaRandom());
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
            result = "Exito";
        } catch (IOException ex) {
            result = "Fallo";
            throw ex;
        }
        return ResponseEntity.of(Optional.of(result));
    }
}
