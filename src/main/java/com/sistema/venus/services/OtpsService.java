package com.sistema.venus.services;

import com.sistema.venus.domain.Otps;
import com.sistema.venus.repo.OtpsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpsService {

    @Autowired
    private OtpsRepository otpsRepository;

    @Value("${codigo.recuperacion.contra.length}")
    private int length;


    public Otps addOtps(Otps _otps){
        _otps.setTiempoExpiracion(LocalDateTime.now());
        _otps.setCodigo(generaRandom());
        return otpsRepository.save(_otps);
    }

    private String generaRandom(){
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder codigo = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int indiceRandom = random.nextInt(letras.length());
            char letraRandom = letras.charAt(indiceRandom);
            codigo.append(letraRandom);
        }
        System.out.println(" codigo.toString() " + codigo.toString());
        return codigo.toString();
    }

    public Otps getOtpsByUserCode(String userCode){
        return otpsRepository.getOtpsByCodigo(userCode);
    }
}
