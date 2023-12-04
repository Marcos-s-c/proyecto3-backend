package com.sistema.venus.services;

import com.sistema.venus.domain.UserPreferences;
import com.sistema.venus.repo.NotificacionesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserPreferenceService {

    @Autowired
    private NotificacionesRepo repo;

    public List<UserPreferences> getAllUserPreferences() {
        return repo.obtTodasPreferencias();
    }

    @Transactional
    public UserPreferences addPrefNotificacion(UserPreferences n) {
        String emailId = n.getEmailId();

        UserPreferences existingNotificacion = repo.getPreferenciaNotificacionByEmail(emailId);

        if (existingNotificacion == null) {
            return repo.save(n);
        } else {
            if(repo.actualizaNotificacion(emailId, n.getEmail(), n.getSms(), n.getWapp()) == 1){
                return n;
            }else {
                return new UserPreferences();
            }
        }
    }

    @Transactional
    public UserPreferences addFrecuencia(UserPreferences n) {
        String emailId = n.getEmailId();

        UserPreferences existingNotificacion = repo.getPreferenciaNotificacionByEmail(emailId);

        if (existingNotificacion == null) {
            return repo.save(n);
        } else {
            if(repo.actualizaPreferencia(emailId, n.getAnticipation_notice()) == 1){
                return n;
            }else {
                return new UserPreferences();
            }
        }
    }
    public UserPreferences getPreferenciaNotificacionByEmail(String email){
        return repo.getPreferenciaNotificacionByEmail(email);
    }

}
