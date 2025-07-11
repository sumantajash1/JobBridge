package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.BasicDto;
import com.Sumanta.JobListing.DTO.CompanyLoginRequestBody;
import com.Sumanta.JobListing.DTO.SingleObject;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Service.CompanyService;
import com.Sumanta.JobListing.Service.OtpService;
import com.Sumanta.JobListing.utils.CookieUtil;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@EnableMethodSecurity
@RequestMapping("/Company")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    @Autowired
    CompanyService companyService;
    @Autowired
    OtpService otpService;
    CookieUtil cookieUtil = new CookieUtil();

    @PostMapping("/SignUp")
    public ResponseEntity<String> SignUp(@RequestBody Company company, HttpServletResponse response) {
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
        Pair<String, String> companyserviceResponse = companyService.Login(companyLoginRequestBody);
        if(companyserviceResponse.getLeft().equals("failed")) {
            return ResponseEntity.badRequest().body(companyserviceResponse.getRight());
        }
        String jwtToken = companyserviceResponse.getRight();
        response.setHeader("jwt", jwtToken);
        response.setHeader(HttpHeaders.SET_COOKIE, cookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(companyserviceResponse.getLeft());
    }

    @GetMapping("/generateOtpByGstNum/{gstNum}")
    public ResponseEntity<String> getOtp(@PathVariable("gstNum") String gstNum) {
        String serviceResponse = otpService.generateOtpByGstNum(gstNum);
        if(serviceResponse.equals("UserNotExist")) {
            return ResponseEntity.badRequest().body(serviceResponse);
        }
        if(serviceResponse.equals("OtpNotGenerated")) {
            return ResponseEntity.badRequest().body("OTP couldn't be generated");
        }
        return ResponseEntity.ok(serviceResponse);
    }

    @GetMapping("/generateOtpByMobNo/{mobNo}")
    public ResponseEntity<String> getOtpbyMobNo(@PathVariable("mobNo") String mobNo) {
       String serviceResponse = otpService.generateOtpbyMobNo(mobNo);
        if(serviceResponse.equals("UserNotExist")) {
            return ResponseEntity.badRequest().body(serviceResponse);
        }
        if(serviceResponse.equals("OtpNotGenerated")) {
            return ResponseEntity.badRequest().body("OTP couldn't be generated");
        }
        return ResponseEntity.ok(serviceResponse);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestBody BasicDto dto) {
        String otpServiceResponse = otpService.verifyOtp(dto.getMobNo(), dto.getCode());
        if(otpServiceResponse.equals("RIGHT")) {
            return ResponseEntity.ok("OTP Verified");
        }
        return ResponseEntity.badRequest().body("Wrong Otp");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody BasicDto dto) {
      Boolean companyServiceResponse = companyService.resetPassword(dto.getMobNo(), dto.getCode());
      if(!companyServiceResponse) {
          return ResponseEntity.badRequest().body("FAILED");
      }
      return ResponseEntity.ok("SUCCESS");
    }

    @PostMapping("/verifyCompanyToken")
    @PreAuthorize("hasRole('Company')") //Change this using pathVariable
    public ResponseEntity<String> verifyCompanyToken(@RequestBody SingleObject payload) {
            return ResponseEntity.ok(companyService.getComapnyName(JwtTokenUtil.getUserIdFromToken(payload.getPayload())));
    }

    @PostMapping("/postJob")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<String> postJob(@RequestBody JobPost jobPost) {
       Pair<String, String> companyServiceResponse = companyService.postJob(jobPost);
       if(companyServiceResponse.getLeft().equals("failed")) {
          return ResponseEntity.ok(companyServiceResponse.getRight());
       }
       String jobId = companyServiceResponse.getRight();
       return ResponseEntity.ok(jobId);
    }

    @GetMapping("/show-all-active-jobs/{companyId}")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<List<JobPost>> showAllActiveJobs(@PathVariable("companyId") String companyId) {
        log.info("SUMANTA : Inside  ");
        try {
            List<JobPost> allActiveJobs = companyService.getAllActiveJobs(companyId);
            return ResponseEntity.ok(allActiveJobs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/show-all-inactive-jobs/{companyId}")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<List<JobPost>> showAllInactiveJobs(@PathVariable("companyId") String companyId) {
        log.info("SUMANTA : Inside  ");
        try {
            List<JobPost> allInactiveJobs = companyService.getAllInactiveJobs(companyId);
            return ResponseEntity.ok(allInactiveJobs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


}
