package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Service.ApplicantService;
import com.Sumanta.JobListing.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/Applicant")
public class ApplicantController {
    @Autowired
    ApplicantService applicantService;

    @PostMapping("/SignUp")
    public ResponseEntity<String> SignUp(@RequestBody Applicant applicant, HttpServletResponse response) {
        System.out.println("SIGNUP" + applicant);
        Pair<String, String> applicantServiceResponse= applicantService.register(applicant);
        if(applicantServiceResponse.getLeft().equals("failed")) {
            return ResponseEntity.ok(applicantServiceResponse.getRight());
        }
        String jwtToken = applicantServiceResponse.getRight();
        response.setHeader( "jwtToken", jwtToken);
        CookieUtil cookieUtil = new CookieUtil();
        response.setHeader(HttpHeaders.SET_COOKIE, cookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(applicantServiceResponse.getLeft());
    }

    @PostMapping("/SignIn")
    public ResponseEntity<String> SignIn(@RequestBody ApplicantLoginRequestBody applicantLoginRequestBody, HttpServletResponse response) {
        System.out.println("SIGNIN" + applicantLoginRequestBody);
        Pair<String, String> applicantServiceResponse = applicantService.Login(applicantLoginRequestBody);
        if(applicantServiceResponse.getLeft().equals("failed")) {
            return ResponseEntity.ok(applicantServiceResponse.getRight());
        }
        String jwtToken = applicantServiceResponse.getRight();
        response.setHeader("jwtToken", jwtToken);
        CookieUtil cookieUtil = new CookieUtil();
        response.setHeader(HttpHeaders.SET_COOKIE, cookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(applicantServiceResponse.getLeft());
    }
}
