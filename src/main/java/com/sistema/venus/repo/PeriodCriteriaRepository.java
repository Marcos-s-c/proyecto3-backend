package com.sistema.venus.repo;

import com.sistema.venus.domain.PeriodCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PeriodCriteriaRepository extends JpaRepository<PeriodCriteria,Long> {
    @Query("SELECT p FROM PeriodCriteria p WHERE p.date = :date and p.fieldName = :fieldName")
    PeriodCriteria getPeriodCriteriaByDateAndFieldName(LocalDate date,String fieldName);
}
