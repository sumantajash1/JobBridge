package com.Sumanta.JobListing.utils;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

public class CookieUtil {
    public static ResponseCookie generateCookie(String jwtToken) {
        return ResponseCookie.from("jwtToken", jwtToken)
                .httpOnly(true)
                .secure(false)
                .maxAge(60*60*60*10)
                .path("/")
                .build();
    }
}
