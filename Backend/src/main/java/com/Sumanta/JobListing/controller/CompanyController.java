package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.CompanyLoginRequestBody;
import com.Sumanta.JobListing.DTO.OtpDto;
import com.Sumanta.JobListing.DTO.SingleObject;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Service.CompanyService;
import com.Sumanta.JobListing.Service.OtpService;
import com.Sumanta.JobListing.utils.CookieUtil;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableMethodSecurity
@RequestMapping("/Company")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    @Autowired
    CompanyService companyService;
    CookieUtil cookieUtil = new CookieUtil();
    @Autowired
    OtpService otpService;

    @PostMapping("/SignUp")
    public ResponseEntity<String> SignUp(@RequestBody Company company, HttpServletResponse response) {
        System.out.println("Sign Up + " + company);
        Pair<String, String> serviceResponse = companyService.register(company);
        if(serviceResponse.getLeft().equals("failed")) {
            return ResponseEntity.ok(serviceResponse.getRight());
        }
        String jwtToken = serviceResponse.getRight();
        response.setHeader("jwt", jwtToken);
        response.setHeader(HttpHeaders.SET_COOKIE, cookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(serviceResponse.getLeft());
    }

    @PostMapping("/SignIn")
    public ResponseEntity<String> SignIn(@RequestBody CompanyLoginRequestBody companyLoginRequestBody, HttpServletResponse response) {
        System.out.println("SignIn + " + companyLoginRequestBody);
        Pair<String, String> companyserviceResponse = companyService.Login(companyLoginRequestBody);
        if(companyserviceResponse.getLeft().equals("failed")) {
            return ResponseEntity.badRequest().body(companyserviceResponse.getRight());
        }
        String jwtToken = companyserviceResponse.getRight();
        response.setHeader("jwt", jwtToken);
        response.setHeader(HttpHeaders.SET_COOKIE, cookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(companyserviceResponse.getLeft());
    }

    @GetMapping("/generateOtp/{gstNum}")
    public ResponseEntity<String> getOtp(@PathVariable("gstNum") String gstNum) {
        String serviceResponse = otpService.generateOtp(gstNum);
        if(serviceResponse.equals("UserNotExist")) {
            return ResponseEntity.badRequest().body(serviceResponse);
        }
        if(serviceResponse.equals("OtpNotGenerated")) {
            return ResponseEntity.badRequest().body("OTP couldn't be generated");
        }
        return ResponseEntity.ok(serviceResponse);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpDto obj) {
        String otpServiceResponse = otpService.verifyOtp(obj.getMobileNum(), obj.getOtp());
        if(otpServiceResponse.equals("RIGHT")) {
            return ResponseEntity.ok("OTP Verified");
        }
        return ResponseEntity.badRequest().body("Wrong Otp");
    }

    @PostMapping("/verifyCompanyToken")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<String> verifyCompanyToken(@RequestBody SingleObject payload) {
        System.out.println("Inside verifyCompanyToken");
            return ResponseEntity.ok(companyService.getComapnyName(JwtTokenUtil.getUserIdFromToken(payload.getPayload())));
    }

    @PostMapping("/postJob")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<String> postJob(@RequestBody JobPost jobPost) {
       // System.out.println(jobPost);
       Pair<String, String> companyServiceResponse = companyService.postJob(jobPost);
       if(companyServiceResponse.getLeft().equals("failed")) {
          return ResponseEntity.ok(companyServiceResponse.getRight());
       }
       String jobId = companyServiceResponse.getRight();
       return ResponseEntity.ok(jobId);
    }

}
