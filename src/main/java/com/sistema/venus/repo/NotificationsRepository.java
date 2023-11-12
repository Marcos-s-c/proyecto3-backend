package com.sistema.venus.repo;

import com.sistema.venus.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT p FROM Notification p JOIN p.user_id u WHERE u.user_id = :userId ORDER BY p.date DESC")
    List<Notification> getNotificationByUserId(Long userId);
}
