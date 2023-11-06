package com.sistema.venus.domain;

import lombok.Data;

@Data
public class PasswordResetChangeRequest {
    private String newPassword;
    private String userCode;
}
