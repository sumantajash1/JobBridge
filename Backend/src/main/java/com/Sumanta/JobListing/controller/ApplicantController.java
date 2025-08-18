package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.*;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Service.ApplicantService;
import com.Sumanta.JobListing.Service.OtpService;
import com.Sumanta.JobListing.Service.ResumeService;
import com.Sumanta.JobListing.Service.impl.ApplicantServiceImpl;
import com.Sumanta.JobListing.Service.impl.OtpServiceImpl;
import com.Sumanta.JobListing.Service.impl.ResumeServiceImpl;
import com.Sumanta.JobListing.utils.CookieUtil;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<ResponseWrapper<AuthResponseDto>> signIn(@RequestBody AuthRequestBody applicantLoginRequestBody, HttpServletResponse response) {
        ResponseWrapper<AuthResponseDto> applicantServiceResponse = applicantService.logIn(applicantLoginRequestBody);
        if(applicantServiceResponse.isSuccess() && applicantServiceResponse.getData() != null) {
            String jwtToken = applicantServiceResponse.getData().getJwtToken();
            response.setHeader("jwtToken", jwtToken);
            response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        }
        return new ResponseEntity<>(applicantServiceResponse, HttpStatus.valueOf(applicantServiceResponse.getHttpStatusCode()));
    }

    @GetMapping("/get-otp/{mobNo}")
    public ResponseEntity<ResponseWrapper<String>> getOtp(@PathVariable("mobNo") String mobNo) {
        ResponseWrapper<String> otpServiceResponse = otpServiceImpl.generateOtpbyMobNo(mobNo);
        return new ResponseEntity<>(otpServiceResponse, HttpStatus.valueOf(otpServiceResponse.getHttpStatusCode()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseWrapper<String>> verifyOtp(@RequestBody AuthRequestBody dto) {
        ResponseWrapper<String> otpServiceResponse = otpServiceImpl.verifyOtp(dto.getId(), dto.getPassword());
        return new ResponseEntity<>(otpServiceResponse, HttpStatus.valueOf(otpServiceResponse.getHttpStatusCode()));
    }

    @GetMapping("/verify-applicant-token")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<ResponseWrapper> verifyApplicantToken() {
        return new ResponseEntity<>(
                new ResponseWrapper(
                        true,
                        200,
                        "Applicant's Jwt Token is verified",
                        null,
                        null
                ),
                HttpStatus.valueOf(200));
    }

    @GetMapping("/get-all-jobs")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<ResponseWrapper<List<JobPost>>> allJobs() { // For Testing purpose only, while deleting, delete service method as well
        ResponseWrapper<List<JobPost>> response = applicantService.fetchAllJobs();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<ResponseWrapper> resetPassword(@RequestBody AuthRequestBody dto) {
        ResponseWrapper response = applicantService.resetPassword(dto.getId(), dto.getPassword());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    @PreAuthorize("hasRole('Applicant')")
    @PostMapping("/jobs/apply")
    public ResponseEntity<ResponseWrapper> applyToJobs(@RequestParam("jobId") String jobId,
                                              @RequestParam("companyId") String companyId,
                                              @RequestParam("resume")MultipartFile resume,
                                              HttpServletRequest request) throws IOException {

        ResponseWrapper applicantServiceResponse = applicantService.applyToJob(jobId,JwtTokenUtil.extractTokenFromRequest(request), companyId, resume);
        return new ResponseEntity<>(applicantServiceResponse, HttpStatus.valueOf(applicantServiceResponse.getHttpStatusCode()));
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

    @GetMapping("/get-all-applications")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<ResponseWrapper<List<ApplicationDto>>> getAllApplications(HttpServletRequest request) {
        ResponseWrapper<List<ApplicationDto>> response = applicantService.getAllApplications(JwtTokenUtil.extractTokenFromRequest(request));
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    @GetMapping("/health-check")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<ResponseWrapper> healthCheck() {
        return new ResponseEntity<>(
                new ResponseWrapper<>(
                        true,
                        200,
                        "Health Check Successful",
                        null,
                        null
                ),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/delete-account")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<ResponseWrapper<Void>> deleteAccount(HttpServletRequest request) {
        ResponseWrapper<Void> serviceResp = applicantService.deleteAccount(JwtTokenUtil.extractTokenFromRequest(request));
        return new ResponseEntity<>(serviceResp, HttpStatus.valueOf(serviceResp.getHttpStatusCode()));
    }

    @DeleteMapping("/remove-application/{applicationId}")
    @PreAuthorize("hasRole('Applicant')")
    public ResponseEntity<ResponseWrapper> withdrawApplication(@PathVariable("applicationId") String applicationId, HttpServletRequest request) {
        ResponseWrapper serviceResp = applicantService.removeApplication(applicationId, JwtTokenUtil.extractTokenFromRequest(request));
        return new ResponseEntity<>(serviceResp, HttpStatus.valueOf(serviceResp.getHttpStatusCode()));
    }
}
