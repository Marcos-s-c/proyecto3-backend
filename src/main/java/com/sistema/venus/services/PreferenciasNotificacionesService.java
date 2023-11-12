package com.sistema.venus.services;

import com.sistema.venus.domain.Notificaciones;
import com.sistema.venus.repo.NotificacionesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PreferenciasNotificacionesService {

    @Autowired
    private NotificacionesRepo repo;

    public Notificaciones addPrefNotificacion(Notificaciones n){
        return repo.save(n);
    }


}
