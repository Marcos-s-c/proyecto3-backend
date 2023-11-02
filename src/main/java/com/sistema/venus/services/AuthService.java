package com.sistema.venus.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sistema.venus.domain.*;
import com.sistema.venus.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private OtpsService otpsService;

    @Value("${spring.sendgrid.api-key}")
    private String sendGridApiKey;
    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;
    public LoginResponse getToken(LoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        String email = authentication.getName();
        User user = new User(email);
        String token = jwtUtil.createToken(user);
        return new LoginResponse(token);
    }

    public ResponseEntity<String> sendEmail(RecuperaContraReqBody body) throws IOException {
        String userId = userService.getIdByEmail(body.getEmail());
        Otps otps = new Otps();

        otps.setUser_id(Long.parseLong(userId));

        String result = "";
        Email from = new Email("squirosv@ucenfotec.ac.cr");
        String subject = "Prueba de venus";
        Email to = new Email(body.getEmail());
        Content content = new Content("text/plain", "Codigo Generado: ");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv(sendGridApiKey));
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        result = "Success: "  + userId;
        otpsService.addOtps(otps);
        return ResponseEntity.of(Optional.of(result));
    }

    public void passwordChange(PasswordResetChangeRequest body) throws ValidationException{
        Otps otps = otpsService.getOtpsByUserCode(body.getUserCode());
        if(LocalDateTime.now().isAfter(otps.getTiempoExpiracion().plusMinutes(15))){
            User user = userService.getUserById(otps.getUser_id());
            user.setPassword(body.getNewPassword());
            userService.saveUser(user);
        }else{
            throw new ValidationException("Expired password change code");
        }
    }
}
