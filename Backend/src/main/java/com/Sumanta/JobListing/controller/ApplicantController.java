package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.DTO.BasicDto;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Service.ApplicantService;
import com.Sumanta.JobListing.Service.OtpService;
import com.Sumanta.JobListing.Service.ResumeService;
import com.Sumanta.JobListing.utils.CookieUtil;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
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
    @Autowired
    private GridFsOperations gridFsOperations;

    @PostMapping("/sign-up")
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

    @PostMapping("/sign-in")
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

    @GetMapping("/get-otp/{mobNo}")
    public ResponseEntity<String> getOtp(@PathVariable("mobNo") String mobNo) {
        String otpServiceResponse = otpService.generateOtpbyMobNo(mobNo);
        if(otpServiceResponse.equals("OtpNotGenerated")) {
            return ResponseEntity.badRequest().body("Otp Couldn't be generated");
        }
        return ResponseEntity.ok(mobNo);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody BasicDto dto) {
        String otpServiceResponse = otpService.verifyOtp(dto.getId(), dto.getCode());
        if(otpServiceResponse.equals("WRONG")) {
            return ResponseEntity.badRequest().body("Wrong Otp");
        }
        return ResponseEntity.ok("Otp has been verified successfully");
    }

    @GetMapping("/verify-applicant-token")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<String> verifyApplicantToken() {
        return ResponseEntity.ok("applicantTokenIsValid");
    }

    @GetMapping("/get-all-jobs")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<List<JobPost>> allJobs() { // For Testing purpose only, while deleting, delete service method as well
        return ResponseEntity.ok(applicantService.fetchAllJobs());
    }

    @PatchMapping("/reset-password")
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


    @GetMapping("/download-resume/{resumeId}")
    @PreAuthorize("hasRole('Applicant')")
    public void downloadResume(@PathVariable("resumeId") String resumeId, HttpServletResponse response) {
        try {
            GridFSFile file = resumeService.getFileById(resumeId);
            if (file == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"");
            response.setContentType(file.getMetadata() != null && file.getMetadata().get("_contentType") != null
                    ? file.getMetadata().get("_contentType").toString()
                    : MediaType.APPLICATION_PDF_VALUE);

            InputStream inputStream = gridFsOperations.getResource(file).getInputStream();
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {

            log.error("Error downloading resume: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Applicant Service is running");
    }
}

