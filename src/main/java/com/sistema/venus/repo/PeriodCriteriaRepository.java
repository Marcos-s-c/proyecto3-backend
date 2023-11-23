package com.sistema.venus.repo;

import com.sistema.venus.domain.PeriodCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PeriodCriteriaRepository extends JpaRepository<PeriodCriteria,Long> {
    @Query("SELECT p FROM PeriodCriteria p JOIN p.userId u WHERE p.date = :date and p.fieldName = :fieldName and u.user_id = :userId")
    PeriodCriteria getPeriodCriteriaByDateAndFieldName(LocalDate date,String fieldName,Long userId);

    @Query(nativeQuery = true,value = "SELECT p.* FROM period_Criteria p WHERE p.user_Id = :userId AND p.date BETWEEN :startDate AND :endDate order by p.date asc")
    List<PeriodCriteria> findByUserIdAndDateBetween(@Param("userId") Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM PeriodCriteria p JOIN p.userId u WHERE p.date = :date and u.user_id = :userId")
    List<PeriodCriteria> getPeriodCriteriaByDate(LocalDate date, Long userId);

    @Query("SELECT p FROM PeriodCriteria p JOIN p.userId u WHERE  u.user_id = :userId")
    List<PeriodCriteria> getPeriodCriteriaByUserId(Long userId);
    @Query(nativeQuery = true, value="SELECT p.* FROM period_Criteria p where p.field_name = :fieldName and p.user_Id = :userId and p.value <> 'NA' order by p.date desc LIMIT 1")
    PeriodCriteria getLastEntryOfPeriodCriteriaByUserIdAndFieldName(@Param("fieldName") String fieldName, @Param("userId") Long userId);

    @Query(nativeQuery = true, value="SELECT p.* FROM period_Criteria p where p.field_name = 'periodCycle' and p.user_Id = :userId and p.value <> 'NA' order by p.date desc LIMIT 13")
    List<PeriodCriteria> getLast6PeriodCycles( @Param("userId") Long userId);

    @Query(nativeQuery = true, value="SELECT p.* FROM period_Criteria p where p.field_name = 'periodCycle' and p.user_Id = :userId and p.value <> 'NA' order by p.date desc LIMIT 4")
    List<PeriodCriteria> getLastPeriodCycle( @Param("userId") Long userId);

    @Query(nativeQuery = true, value="SELECT p.* FROM period_Criteria p where p.field_name = :fieldname and p.user_Id = :userId and p.date <= :maxDate and p.date >= :minDate order by p.date asc")
    List<PeriodCriteria> getPeriodCriteriaLastPeriodCycle( @Param("fieldname") String fieldname,  @Param("userId") Long userId, @Param("maxDate") LocalDate maxDate, @Param("minDate") LocalDate minDate);
    @Query(nativeQuery = true, value="SELECT p.* FROM period_Criteria p where p.field_name = 'periodCycle' and p.user_Id = :userId and p.date = :date")
    PeriodCriteria getPeriodCycleByDateAndUser( @Param("userId") Long userId, @Param("date") LocalDate date);
}
