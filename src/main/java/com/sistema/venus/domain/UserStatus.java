package com.sistema.venus.domain;

public class UserStatus {
    private Long user_id;
    private Boolean active;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public UserStatus() {
    }

    public UserStatus(Long user_id, Boolean active) {
        this.user_id = user_id;
        this.active = active;
    }
}
