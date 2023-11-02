package com.sistema.venus.controller;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sistema.venus.domain.*;
import com.sistema.venus.services.AuthService;
import com.sistema.venus.services.OtpsService;
import com.sistema.venus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rest/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpsService otpsService;

    @Value("${spring.sendgrid.api-key}")
    private String sendGridApiKey;
    @PostMapping(value = "login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {

        try {
            // Autenticar al usuario
            LoginResponse loginRes = authService.getToken(loginRequest);

            // Cargar información adicional del usuario
            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());

            // Combina la respuesta de inicio de sesión con los detalles del usuario en un mapa
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", loginRes.getToken());
            responseMap.put("user", userDetails);

            return ResponseEntity.ok(responseMap);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(value = "register")
    public ResponseEntity register(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.createUser(user));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "/recuperaContra")
    public ResponseEntity<String> recuperarContra(@RequestBody RecuperaContraReqBody body) throws IOException {
        String userId = userService.getIdByEmail(body.getEmail());
        Otps otps = new Otps();

        otps.setUser_id(Long.parseLong(userId));

        String result = "";
        Email from = new Email("squirosv@ucenfotec.ac.cr");
        String subject = "Prueba de venus";
        Email to = new Email(body.getEmail());
        //Content content = new Content("text/plain", "Codigo Generado: ");
        Content content = new Content("text/plain", "Codigo Generado: ");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv(sendGridApiKey));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
//            System.out.println(response.getStatusCode());
//            System.out.println(response.getBody());
//            System.out.println(response.getHeaders());
            result = "Exito id"  + userId;
            otpsService.addOtps(otps);
        } catch (IOException ex) {
            result = "Fallo id"  + userId;
            throw ex;
        }
        return ResponseEntity.of(Optional.of(result));
    }
}
