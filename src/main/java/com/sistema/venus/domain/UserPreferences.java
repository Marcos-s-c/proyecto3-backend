package com.sistema.venus.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPreferenceId;
    private String emailId;
    private String wapp;
    private String sms;
    private String email;
    private int anticipation_notice;

    public UserPreferences() {
    }

    public UserPreferences(Long userPreferenceId, String emailId, String wapp, String sms, String email, int anticipation_notice, Set<Post> likedPosts) {
        this.userPreferenceId = userPreferenceId;
        this.emailId = emailId;
        this.wapp = wapp;
        this.sms = sms;
        this.email = email;
        this.anticipation_notice = anticipation_notice;
        this.likedPosts = likedPosts;
    }

    public UserPreferences(Long userPreferenceId, String emailId, String wapp, String sms, String email, int anticipation_notice) {
        this.userPreferenceId = userPreferenceId;
        this.emailId = emailId;
        this.wapp = wapp;
        this.sms = sms;
        this.email = email;
        this.anticipation_notice = anticipation_notice;
    }

    public UserPreferences(String email){
        this.emailId = email;
    }

    @JsonBackReference
    @ManyToMany(mappedBy = "likes", fetch = FetchType.LAZY)
    private Set<Post> likedPosts;

    @Override
    public String toString() {
        return "UserPreferences{" +
                "userPreferenceId=" + userPreferenceId +
                ", emailId='" + emailId + '\'' +
                ", wapp='" + wapp + '\'' +
                ", sms='" + sms + '\'' +
                ", email='" + email + '\'' +
                ", anticipation_notice=" + anticipation_notice +
                ", likedPosts=" + likedPosts +
                '}';
    }

    public Long getUserPreferenceId() {
        return userPreferenceId;
    }

    public void setUserPreferenceId(Long userPreferenceId) {
        this.userPreferenceId = userPreferenceId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getWapp() {
        return wapp;
    }

    public void setWapp(String wapp) {
        this.wapp = wapp;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Post> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Set<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public int getAnticipation_notice() {
        return anticipation_notice;
    }

    public void setAnticipation_notice(int frecuenciaNotificaciones) {
        this.anticipation_notice = frecuenciaNotificaciones;
    }
}
