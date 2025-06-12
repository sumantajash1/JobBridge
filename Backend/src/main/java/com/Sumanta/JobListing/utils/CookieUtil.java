package com.Sumanta.JobListing.utils;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static ResponseCookie generateCookie(String jwtToken) {
        return ResponseCookie.from("jwtToken", jwtToken)
                .httpOnly(false)
                .secure(false)
                .maxAge(60*60*60*10)
                .path("/")
                .build();
    }
}
