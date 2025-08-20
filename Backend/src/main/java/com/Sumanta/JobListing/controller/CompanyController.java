package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.*;
import com.Sumanta.JobListing.Entity.Application;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Entity.applicationStatus;
import com.Sumanta.JobListing.Service.impl.CompanyServiceImpl;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@EnableMethodSecurity
@RequestMapping("/Company")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    @Autowired
    CompanyServiceImpl companyService;
    @Autowired
    OtpServiceImpl otpServiceImpl;
    @Autowired
    ResumeServiceImpl resumeService;
    @Autowired
    private GridFsOperations gridFsOperations;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<AuthResponseDto>> signUp(@RequestBody Company company, HttpServletResponse response) {
        ApiResponse<AuthResponseDto> serviceResponse = companyService.register(company);
        if(serviceResponse.isSuccess()) {
            String jwtToken = serviceResponse.getData().getJwtToken();
            response.setHeader("jwt", jwtToken);
            response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        }
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<AuthResponseDto>> signIn(@RequestBody AuthRequestBody authRequestBody, HttpServletResponse response) {
        ApiResponse<AuthResponseDto> serviceResponse = companyService.login(authRequestBody);
        if(serviceResponse.isSuccess()) {
            String jwtToken = serviceResponse.getData().getJwtToken();
            response.setHeader("jwt", jwtToken);
            response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        }
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @GetMapping("/generate-otp-gst-num/{gstNum}")
    public ResponseEntity<ApiResponse<String>> getOtp(@PathVariable("gstNum") String gstNum) {
        ApiResponse<String> serviceResponse = otpServiceImpl.generateOtpByGstNum(gstNum);
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @GetMapping("/generate-otp-mob-no/{mobNo}")
    public ResponseEntity<ApiResponse<String>> getOtpByMobNo(@PathVariable("mobNo") String mobNo) {
       ApiResponse<String> serviceResponse = otpServiceImpl.generateOtpByMobNo(mobNo);
       return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody AuthRequestBody dto) {
        ApiResponse<String> otpServiceResponse = otpServiceImpl.verifyOtp(dto.getId(), dto.getPassword());
        return new ResponseEntity<>(otpServiceResponse, HttpStatus.valueOf(otpServiceResponse.getHttpStatusCode()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody AuthRequestBody dto) {
      ApiResponse<String> serviceResponse = companyService.resetPassword(dto.getId(), dto.getPassword());
      return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @GetMapping("/verify-company-token/{jwtToken}")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<ApiResponse<String>> verifyCompanyToken(HttpServletRequest request) {
        ApiResponse<String> serviceResponse = companyService.getCompanyName(JwtTokenUtil.getUserIdFromToken(JwtTokenUtil.extractTokenFromRequest(request)));
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @PostMapping("/post-job")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<ApiResponse<String>> postJob(@RequestBody JobPost jobPost, HttpServletRequest request) {
       ApiResponse<String> serviceResponse = companyService.postJob(jobPost, JwtTokenUtil.extractTokenFromRequest(request));
       return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @GetMapping("/show-all-active-jobs")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<ApiResponse<List<JobPost>>> showAllActiveJobs(HttpServletRequest request) {
        ApiResponse<List<JobPost>> serviceResponse = companyService.getAllActiveJobs(JwtTokenUtil.extractTokenFromRequest(request));
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @GetMapping("/show-all-inactive-jobs")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<ApiResponse<List<JobPost>>> showAllInactiveJobs(HttpServletRequest request) {
        ApiResponse<List<JobPost>> serviceResponse = companyService.getAllInactiveJobs(JwtTokenUtil.extractTokenFromRequest(request));
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @GetMapping("/job-all-applications/{jobId}")
    @PreAuthorize(("hasRole('Company')"))
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> showAllApplicationsForJobId(@PathVariable("jobId") String jobId, HttpServletRequest request) {
        ApiResponse<List<ApplicationDto>> serviceResponse = companyService.getAllApplicationsForJob(jobId, JwtTokenUtil.extractTokenFromRequest(request));
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @PatchMapping("/set-job-status")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<ApiResponse<Void>> setJobStatus(@RequestParam("jobId") String jobId, @RequestParam("status") boolean status, HttpServletRequest request) {
        if(jobId == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, 400, "Job ID or Status is missing", null, null), HttpStatus.BAD_REQUEST);
        }
        ApiResponse<Void> serviceResponse = companyService.setJobStatus(jobId, status, JwtTokenUtil.extractTokenFromRequest(request));
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @PatchMapping("/set-application-status")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<ApiResponse<Void>> setApplicationStatus(@RequestParam("applicationId") String applicationId, @RequestParam("status") applicationStatus status, HttpServletRequest request) {
        if(applicationId == null || status == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, 400, "Application ID or Status is missing", null, null), HttpStatus.BAD_REQUEST);
        }
        ApiResponse<Void> serviceResponse = companyService.setApplicationStatus(applicationId, status, JwtTokenUtil.extractTokenFromRequest(request));
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @GetMapping("/view-all-selected-applications/{jobId}")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<ApiResponse<List<Application>>> viewAllSelectedApplications(@PathVariable("jobId") String jobId, HttpServletRequest request) {
        ApiResponse<List<Application>> serviceResponse = companyService.getAllSelectedApplicationsForJob(JwtTokenUtil.extractTokenFromRequest(request), jobId);
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @GetMapping("/download-resume/{resumeId}")
    @PreAuthorize("hasRole('Company')")
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

    @DeleteMapping("/delete-job")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@RequestParam("jobId") String jobId, HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization").substring(7);
        ApiResponse<Void> serviceResponse = companyService.deleteJob(jobId, jwtToken);
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getHttpStatusCode()));
    }

    @GetMapping("/health-check")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Company Service is running");
    }

}
