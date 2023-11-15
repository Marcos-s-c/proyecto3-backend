package com.sistema.venus.repo;

import com.sistema.venus.domain.Medication;
import com.sistema.venus.domain.PeriodCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication,Long> {
    @Query("SELECT p FROM Medication p JOIN p.user u WHERE  u.user_id = :userId")
    List<Medication> getMedicationByUserId(Long userId);
}
