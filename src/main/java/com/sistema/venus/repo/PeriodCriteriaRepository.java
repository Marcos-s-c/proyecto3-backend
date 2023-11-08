package com.sistema.venus.repo;

import com.sistema.venus.domain.PeriodCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PeriodCriteriaRepository extends JpaRepository<PeriodCriteria,Long> {
    @Query("SELECT p FROM PeriodCriteria p JOIN p.userId u WHERE p.date = :date and p.fieldName = :fieldName and u.user_id = :userId")
    PeriodCriteria getPeriodCriteriaByDateAndFieldName(LocalDate date,String fieldName,Long userId);

    @Query("SELECT p FROM PeriodCriteria p JOIN p.userId u WHERE p.date = :date and u.user_id = :userId")
    List<PeriodCriteria> getPeriodCriteriaByDate(LocalDate date, Long userId);

    @Query("SELECT p FROM PeriodCriteria p JOIN p.userId u WHERE  u.user_id = :userId")
    List<PeriodCriteria> getPeriodCriteriaByUserId(Long userId);
}
