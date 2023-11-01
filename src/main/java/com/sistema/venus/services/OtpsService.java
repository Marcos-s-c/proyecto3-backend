package com.sistema.venus.services;

import com.sistema.venus.domain.Otps;
import com.sistema.venus.repo.OtpsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OtpsService {

    @Autowired
    OtpsRepository otpsRepository;

    public Otps addOtps(Otps _otps){
        _otps.setTiempoExpiracion(tiempoExpiracion());
        return otpsRepository.save(_otps);
    }

    public LocalDateTime tiempoExpiracion(){
        LocalDateTime fechaActual = LocalDateTime.now();
        LocalDateTime fechaMas15 = fechaActual.plusMinutes(15);
        return fechaMas15;
    }


}
