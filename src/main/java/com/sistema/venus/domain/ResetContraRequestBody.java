package com.sistema.venus.domain;

public class ResetContraRequestBody {
    private String email;
    private String string;

    public ResetContraRequestBody(String email, String string) {
        this.email = email;
        this.string = string;
    }

    public ResetContraRequestBody() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return "ResetContraRequestBody{" +
                "email='" + email + '\'' +
                ", string='" + string + '\'' +
                '}';
    }
}
