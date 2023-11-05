package com.sistema.venus.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "period_criteria")
@NoArgsConstructor
@AllArgsConstructor
public class PeriodCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long periodCriteriaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User userId;
    private String fieldName;
    private String value;
    private LocalDate date;
}
