package com.Sumanta.JobListing.controller;

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
        System.out.println(applicant);
        String applicantServiceResponse = applicantService.register(applicant);
        if(applicantServiceResponse.equals("AlreadyExists")) {
            return ResponseEntity.ok("User Already Exists");
        }
        String jwtToken = applicantServiceResponse;
        response.setHeader( "jwtToken", jwtToken);
        return ResponseEntity.ok(jwtToken);
    }
}
