package com.sistema.venus.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "medications")
@NoArgsConstructor
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicine_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;
    private String name;
    private String dosis;
    private String frecuencia;
  
    public Long getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(Long medicine_id) {
        this.medicine_id = medicine_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User userId) {
        this.user = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }
}
