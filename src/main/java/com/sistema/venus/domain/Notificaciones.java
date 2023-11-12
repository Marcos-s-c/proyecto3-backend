package com.sistema.venus.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "notificaciones")
public class Notificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String emailId;
    private String wapp;
    private String sms;
    private String email;

    public Notificaciones(Integer id, String emailId, String wapp, String sms, String email) {
        this.id = id;
        this.emailId = emailId;
        this.wapp = wapp;
        this.sms = sms;
        this.email = email;
    }

    public Notificaciones() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String user_id) {
        this.emailId = user_id;
    }

    public String getWapp() {
        return wapp;
    }

    public void setWapp(String wappSelected) {
        this.wapp = wappSelected;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String smsSelected) {
        this.sms = smsSelected;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String emailSelected) {
        this.email = emailSelected;
    }


}
