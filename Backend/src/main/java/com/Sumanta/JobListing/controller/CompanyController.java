package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
@EnableMethodSecurity
@RequestMapping("/Company")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @PostMapping("/SignUp")
    public ResponseEntity<String> SignUp(@RequestBody Company company) {
        System.out.println("Entered SignUp method of CompanyController");
         Boolean registered = companyService.register(company);
         if(registered) {
             return ResponseEntity.ok("Company registered Successfully");
         }
         return ResponseEntity.ok("Company registration Unsuccessful");
    }
}
