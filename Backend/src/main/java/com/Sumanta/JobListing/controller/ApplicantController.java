package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Service.ApplicantService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
        String applicantServiceResponse = applicantService.register(applicant);
        if(applicantServiceResponse.equals("PhoneExists")) {
            return ResponseEntity.ok("Phone number Already Exists");
        }
        if(applicantServiceResponse.equals("EmailExists")) {
            return ResponseEntity.ok("Email already exists");
        }
        String jwtToken = applicantServiceResponse;
        response.setHeader( "jwtToken", jwtToken);
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/SignIn")
    public ResponseEntity<String> SignIn(@RequestBody ApplicantLoginRequestBody applicantLoginRequestBody, HttpServletResponse response) {
        System.out.println("SIGNIN" + applicantLoginRequestBody);
        String applicantServiceResponse = applicantService.Login(applicantLoginRequestBody);
        if(applicantServiceResponse.equals("Doesn't Exist")) {
            return ResponseEntity.ok(applicantServiceResponse);
        }
        if(applicantServiceResponse.equals("Wrong Password")) {
            return ResponseEntity.ok(applicantServiceResponse);
        }
        String jwtToken = applicantServiceResponse;
        response.setHeader("jwtToken", applicantServiceResponse);
        return ResponseEntity.ok(jwtToken);
    }
}
