package com.sistema.venus.repo;

import com.sistema.venus.domain.Notificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionesRepo extends JpaRepository<Notificaciones, Integer> {
    @Query("select n from Notificaciones n where n.emailId = :email")
    Notificaciones getPreferenciaNotificacionByEmail(@Param("email") String email);
    @Modifying
    @Query("update Notificaciones n set n.email = :email, n.sms = :sms, n.wapp = :wapp where n.emailId = :emailId")
    int actualizaNotificacion(
            @Param("emailId") String emailId,
            @Param("email") String email,
            @Param("sms") String sms,
            @Param("wapp") String wapp
    );

}
