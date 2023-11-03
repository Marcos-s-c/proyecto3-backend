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

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

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
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("venus49117413@gmail.com", "utga reaz otcq vucf");
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("venus49117413@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(body.getEmail()));
            message.setSubject("This is the email subject");
            message.setText("This is the email body");
            Transport.send(message);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public void passwordChange(PasswordResetChangeRequest body) throws ValidationException {
        Otps otps = otpsService.getOtpsByUserCode(body.getUserCode());
        if (LocalDateTime.now().isAfter(otps.getTiempoExpiracion().plusMinutes(15))) {
            User user = userService.getUserById(otps.getUser_id());
            user.setPassword(body.getNewPassword());
            userService.saveUser(user);
        } else {
            throw new ValidationException("Expired password change code");
        }
    }
}
