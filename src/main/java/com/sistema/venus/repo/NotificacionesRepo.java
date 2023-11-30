package com.sistema.venus.repo;

import com.sistema.venus.domain.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionesRepo extends JpaRepository<UserPreferences, Integer> {
    @Query("select n from UserPreferences n where n.emailId = :email")
    UserPreferences getPreferenciaNotificacionByEmail(@Param("email") String email);

    @Modifying
    @Query("update UserPreferences n set n.email = :email, n.sms = :sms, n.wapp = :wapp where n.emailId = :emailId")
    int actualizaNotificacion(
            @Param("emailId") String emailId,
            @Param("email") String email,
            @Param("sms") String sms,
            @Param("wapp") String wapp
    );

    @Modifying
    @Query("update UserPreferences n set n.anticipation_notice = :anticipation_notice where n.emailId = :emailId")
    int actualizaPreferencia(
            @Param("emailId") String emailId,
            @Param("anticipation_notice") Integer anticipation_notice
    );

}
