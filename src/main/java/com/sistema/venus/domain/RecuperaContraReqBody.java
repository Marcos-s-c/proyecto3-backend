package com.sistema.venus.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
public class RecuperaContraReqBody implements Serializable {
    private String email;

    public RecuperaContraReqBody(String email) {
        this.email = email;
    }

    public RecuperaContraReqBody() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "RecuperaContraReqBody{" +
                "email='" + email + '\'' +
                '}';
    }
}
