package com.sistema.venus.util;

import java.time.*;
import java.util.Base64;

public class Utils {
    public static final String USER_ROLE = "USER";

    public static String passwordEncoder(String password){
        return  Base64.getEncoder().encodeToString(password.getBytes());
    }

    public static String passwordDecoder(String encodedPassword) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
        return new String(decodedBytes);
    }


    public static LocalDate getDateCurrentTimezone() {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
        ZoneId zId = ZoneId.of("US/Central");
        return LocalDateTime.ofInstant(zdt.toInstant(), zId).toLocalDate();
    }
}
