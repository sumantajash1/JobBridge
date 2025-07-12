package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.DTO.BasicDto;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Service.ApplicantService;
import com.Sumanta.JobListing.Service.OtpService;
import com.Sumanta.JobListing.Service.ResumeService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/Applicant")
public class ApplicantController {
    @Autowired
    private ApplicantService applicantService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private ResumeService resumeService;

    @PostMapping("/SignUp")
    public ResponseEntity<String> signUp(@RequestBody Applicant applicant, HttpServletResponse response) {
        Pair<String, String> applicantServiceResponse= applicantService.register(applicant);
        if(applicantServiceResponse.getLeft().equals("failed")) {
            return ResponseEntity.badRequest().body(applicantServiceResponse.getRight());
        }
        String jwtToken = applicantServiceResponse.getRight();
        response.setHeader( "jwtToken", jwtToken);
        response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(applicantServiceResponse.getLeft());
    }

    @PostMapping("/SignIn")
    public ResponseEntity<String> signIn(@RequestBody ApplicantLoginRequestBody applicantLoginRequestBody, HttpServletResponse response) {
        Pair<String, String> applicantServiceResponse = applicantService.Login(applicantLoginRequestBody);
        if(applicantServiceResponse.getLeft().equals("failed")) {
            return ResponseEntity.badRequest().body(applicantServiceResponse.getRight());
        }
        String jwtToken = applicantServiceResponse.getRight();
        response.setHeader("jwtToken", jwtToken);
        response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(applicantServiceResponse.getLeft());
    }

    @GetMapping("/getOtp/{mobNo}")
    public ResponseEntity<String> getOtp(@PathVariable("mobNo") String mobNo) {
        String otpServiceResponse = otpService.generateOtpbyMobNo(mobNo);
        if(otpServiceResponse.equals("OtpNotGenerated")) {
            return ResponseEntity.badRequest().body("Otp Couldn't be generated");
        }
        return ResponseEntity.ok(mobNo);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestBody BasicDto dto) {
        String otpServiceResponse = otpService.verifyOtp(dto.getId(), dto.getCode());
        if(otpServiceResponse.equals("WRONG")) {
            return ResponseEntity.badRequest().body("Wrong Otp");
        }
        return ResponseEntity.ok("Otp has been verified successfully");
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

    @PatchMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody BasicDto dto) {
        String result = applicantService.resetPassword(dto.getId(), dto.getCode());
        if (result.equals("ApplicantNotFound")) {
            return ResponseEntity.badRequest().body("Applicant not found");
        }
        return ResponseEntity.ok("Password has been reset successfully");
    }

    @PreAuthorize("hasRole('Applicant')")
    @PostMapping("/jobs/apply")
    public ResponseEntity<String> applyToJobs(@RequestParam("jobId") String jobId,
                                              @RequestParam("applicantId") String applicantId,
                                              @RequestParam ("applicantName") String applicantName,
                                              @RequestParam("companyId") String companyId,
                                              @RequestParam("companyName") String companyName,
                                              @RequestParam("resume")MultipartFile resume) throws IOException {

        String resumeId = resumeService.uploadResume(resume);
        String applicantServiceResponse = applicantService.applyToJob(jobId, applicantId, applicantName, companyId, companyName, resumeId);
        if(applicantServiceResponse.equals("alreadyApplied")) {
            return ResponseEntity.badRequest().body("Already applied to this job");
        }
        if(applicantServiceResponse.equals("JobDontExist")) {
            return ResponseEntity.badRequest().body("This Job has been removed by the employer  ");
        }
        return ResponseEntity.ok("Successfully Applied");
    }
}
