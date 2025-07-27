package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.BasicDto;
import com.Sumanta.JobListing.DTO.ResponseWrapper;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Service.ApplicantService;
import com.Sumanta.JobListing.Service.OtpService;
import com.Sumanta.JobListing.Service.ResumeService;
import com.Sumanta.JobListing.utils.CookieUtil;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ResponseWrapper<AuthResponseDto>> signUp(@RequestBody Applicant applicant, HttpServletResponse response) {
        ResponseWrapper<AuthResponseDto> applicantServiceResponse= applicantService.register(applicant);
        if(applicantServiceResponse.isSuccess() && applicantServiceResponse.getData() != null) {
            String jwtToken = applicantServiceResponse.getData().getJwtToken();
            response.setHeader( "jwtToken", jwtToken);
            response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        }
        return new ResponseEntity<>(applicantServiceResponse,HttpStatus.valueOf(applicantServiceResponse.getHttpStatusCode()));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseWrapper<AuthResponseDto>> signIn(@RequestBody ApplicantLoginRequestBody applicantLoginRequestBody, HttpServletResponse response) {
        ResponseWrapper<AuthResponseDto> applicantServiceResponse = applicantService.logIn(applicantLoginRequestBody);
        if(applicantServiceResponse.isSuccess() && applicantServiceResponse.getData() != null) {
            String jwtToken = applicantServiceResponse.getData().getJwtToken();
            response.setHeader("jwtToken", jwtToken);
            response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        }
        return new ResponseEntity<>(applicantServiceResponse, HttpStatus.valueOf(applicantServiceResponse.getHttpStatusCode()));
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
                                              @RequestParam("companyId") String companyId,
                                              @RequestParam("resume")MultipartFile resume,
                                              HttpServletRequest request) throws IOException {

        String resumeId = resumeService.uploadResume(resume);
        String applicantServiceResponse = applicantService.applyToJob(jobId,JwtTokenUtil.getUserIdFromToken(JwtTokenUtil.extractTokenFromRequest(request)), companyId, resumeId);
        if(applicantServiceResponse.equals("ApplicantNotFound")) {
            return ResponseEntity.badRequest().body("Applicant not found");
        }
        if(applicantServiceResponse.equals("alreadyApplied")) {
            return ResponseEntity.badRequest().body("Already applied to this job");
        }
        if(applicantServiceResponse.equals("JobDontExist")) {
            return ResponseEntity.badRequest().body("This Job Don't Exist / has been removed by the employer");
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

    @GetMapping("/get-all-applications/{applicantId}")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<List<ApplicationDto>> getAllApplications(@PathVariable("applicantId") String applicantId) {
        List<ApplicationDto> applications = applicantService.getAllApplications(applicantId);
        if (applications.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/health-check")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Applicant Service is running");
    }

    @DeleteMapping("/delete-account")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<String> deleteAccount(HttpServletRequest request) {
         String serviceResp = applicantService.deleteAccount(JwtTokenUtil.extractTokenFromRequest(request));
         if(serviceResp.equals("error")) {
             return ResponseEntity.badRequest().body("Account couldn't be deleted");
         }
         return ResponseEntity.ok("account has been deleted");
    }
}
