package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.CompanyLoginRequestBody;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Service.CompanyService;
import com.Sumanta.JobListing.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
            return ResponseEntity.ok(companyserviceResponse.getRight    ());
        }
        String jwtToken = companyserviceResponse.getRight();
        response.setHeader("jwt", jwtToken);
        return ResponseEntity.ok(companyserviceResponse.getLeft());
    }

}
