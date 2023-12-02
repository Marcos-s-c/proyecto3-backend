package com.sistema.venus.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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
    @Column(columnDefinition = "boolean default true")
    private Boolean active;
    private Boolean hasDevice;

    @OneToMany(mappedBy ="user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Medication> medications;
    private String phone;

    public User(String email){
        this.email = email;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public User(Long user_id, String email, String password, String name, String rol, Boolean active, Boolean hasDevice, List<Medication> medications, String phone) {
        this.user_id = user_id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.rol = rol;
        this.active = active;
        this.hasDevice = hasDevice;
        this.medications = medications;
        this.phone = phone;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getHasDevice() {
        return hasDevice;
    }

    public User(Long user_id, String email, String name, Boolean active, Boolean hasDevice, String phone) {
        this.user_id = user_id;
        this.email = email;
        this.name = name;
        this.active = active;
        this.hasDevice = hasDevice;
        this.phone = phone;
    }

    public void setHasDevice(Boolean hasDevice) {
        this.hasDevice = hasDevice;
    }
}