package com.sistema.venus.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sistema.venus.domain.*;
import com.sistema.venus.util.Utils;
import com.sistema.venus.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Value("${frontend.host}")
    String frontendHost;

    @Autowired
    private OtpsService otpsService;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JavaMailSender javaMailSender;


    public LoginResponse getToken(LoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), Utils.passwordEncoder(loginRequest.getPassword())));
        String email = authentication.getName();
        User user = new User(email);
        String token = jwtUtil.createToken(user);
        return new LoginResponse(token);
    }

    public ResponseEntity<String> sendEmail(RecuperaContraReqBody body) {
        try {
            Long userId = userService.getIdByEmail(body.getEmail());
            if(userId == null) return ResponseEntity.ok("Success");
            Otps otps = new Otps();
            otps.setUser_id(userId);
            String codigo =  otpsService.addOtps(otps).getCodigo();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(body.getEmail());
            message.setSubject("Soporte Venus");
            message.setText(String.format("Hola,\n" +
                    "Visite este enlace para recuperar la contraseña:\n" +
                    "%s/password_reset/%s",frontendHost, codigo));
            javaMailSender.send(message);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> sendEmailSendGrid(RecuperaContraReqBody body){
        try {
            Email from = new Email("fretanah@ucenfotec.ac.cr");
            String subject = "Desde SendGrid";
            Email to = new Email("die19h.g@hotmail.com");
            // die19h.g@hotmail.com
            //fabian.retana@hotmail.com

            SendGrid sg = new SendGrid("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");
            Request request = new Request();

            Long userId = userService.getIdByEmail(body.getEmail());
            if(userId == null) return ResponseEntity.ok("Success");
            Otps otps = new Otps();
            otps.setUser_id(userId);
            String codigo =  otpsService.addOtps(otps).getCodigo();

            Content content = new Content("text/plain", String.format("Hola,\n" +
                    "Visite este enlace para recuperar la contraseña:\n" +
                    "%s/password_reset/%s",frontendHost, codigo));
            Mail mail = new Mail(from, subject, to, content);

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public void passwordChange(PasswordResetChangeRequest body) throws ValidationException {
        Otps otps = otpsService.getOtpsByUserCode(body.getUserCode());
        if (!LocalDateTime.now().isAfter(otps.getTiempoExpiracion().plusMinutes(15))) {
            User user = userService.getUserById(otps.getUser_id());
            user.setPassword(Utils.passwordEncoder(body.getNewPassword()));
            userService.saveUser(user);
        } else {
            throw new ValidationException("Expired password change code");
        }
    }

    public ResponseEntity<Object> registerUser(User user) {
        if (userService.isEmailInUse(user.getEmail())) {
            throw new RuntimeException("El correo ya está en uso.");
        }
        user.setRol(Utils.USER_ROLE);
        user.setActive(true);
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

}
