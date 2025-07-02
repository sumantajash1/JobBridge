package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Service.ApplicantService;
import com.Sumanta.JobListing.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
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
        response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(applicantServiceResponse.getLeft());
    }



    @GetMapping("/verifyApplicantToken")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<String> verifyApplicantToken() {
        return ResponseEntity.ok("applicantTokenIsValid");
    }

    @GetMapping("/getAllJobs")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<List<JobPost>> allJobs() { // For Testing purpose only, while deleting, delete service method as well
        return ResponseEntity.ok(applicantService.fetchAllJobs());
    }
}
