package com.sistema.venus.services;

import com.sistema.venus.domain.Notificaciones;
import com.sistema.venus.repo.NotificacionesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PreferenciasNotificacionesService {

    @Autowired
    private NotificacionesRepo repo;
    @Transactional
    public Notificaciones addPrefNotificacion(Notificaciones n) {
        String emailId = n.getEmailId();

        Notificaciones existingNotificacion = repo.getPreferenciaNotificacionByEmail(emailId);

        if (existingNotificacion == null) {
            return repo.save(n);
        } else {
            if(repo.actualizaNotificacion(emailId, n.getEmail(), n.getSms(), n.getWapp()) == 1){
                return n;
            }else {
                return new Notificaciones();
            }
        }
    }
    public Notificaciones getPreferenciaNotificacionByEmail(String email){
        return repo.getPreferenciaNotificacionByEmail(email);
    }

}
