package com.sistema.venus.util;

import java.util.Base64;

public class Utils {
    public static final String USER_ROLE = "USER";

    public static String passwordEncoder(String password){
        return  Base64.getEncoder().encodeToString(password.getBytes());
    }

}
