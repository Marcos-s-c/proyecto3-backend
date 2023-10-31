package com.sistema.venus.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;
    private String email;
    private String password;
    private String name;
    private String rol;
    private Boolean active;
    private LocalDate dob;
    private Double weight;
    private Double height;
    private String phone;


    public User(String email){
        this.email = email;
    }

}