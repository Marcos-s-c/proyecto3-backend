package com.sistema.venus.repo;

import com.sistema.venus.domain.Notificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionesRepo extends JpaRepository<Notificaciones, Integer> {
}
