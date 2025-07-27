package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.BasicDto;
import com.Sumanta.JobListing.DTO.CompanyLoginRequestBody;
import com.Sumanta.JobListing.Entity.Application;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Entity.applicationStatus;
import com.Sumanta.JobListing.Service.CompanyService;
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
    CompanyService companyService;
    @Autowired
    OtpService otpService;
    @Autowired
    ResumeService resumeService;
    @Autowired
    private GridFsOperations gridFsOperations;

    @PostMapping("/sign-up")
    public ResponseEntity<String> SignUp(@RequestBody Company company, HttpServletResponse response) {
        Pair<String, String> serviceResponse = companyService.register(company);
        if(serviceResponse.getLeft().equals("failed")) {
            return ResponseEntity.badRequest().body(serviceResponse.getRight());
        }
        String jwtToken = serviceResponse.getRight();
        response.setHeader("jwt", jwtToken);
        response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(serviceResponse.getLeft());
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> SignIn(@RequestBody CompanyLoginRequestBody companyLoginRequestBody, HttpServletResponse response) {
        Pair<String, String> companyserviceResponse = companyService.Login(companyLoginRequestBody);
        if(companyserviceResponse.getLeft().equals("failed")) {
            return ResponseEntity.badRequest().body(companyserviceResponse.getRight());
        }
        String jwtToken = companyserviceResponse.getRight();
        response.setHeader("jwtToken", jwtToken);
        response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.generateCookie(jwtToken).toString());
        return ResponseEntity.ok(companyserviceResponse.getLeft());
    }

    @GetMapping("/generate-otp-gst-num/{gstNum}")
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

    @GetMapping("/generate-otp-mob-no/{mobNo}")
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

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody BasicDto dto) {
        String otpServiceResponse = otpService.verifyOtp(dto.getId(), dto.getCode());
        if(otpServiceResponse.equals("RIGHT")) {
            return ResponseEntity.ok("OTP Verified");
        }
        return ResponseEntity.badRequest().body("Wrong Otp");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody BasicDto dto) {
      Boolean companyServiceResponse = companyService.resetPassword(dto.getId(), dto.getCode());
      if(!companyServiceResponse) {
          return ResponseEntity.badRequest().body("FAILED");
      }
      return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("/verify-company-token/{jwtToken}")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<String> verifyCompanyToken(@PathVariable("jwtToken") String jwtToken) {
            return ResponseEntity.ok(companyService.getComapnyName(JwtTokenUtil.getUserIdFromToken(jwtToken)));
    }

    @PostMapping("/post-job")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<String> postJob(@RequestBody JobPost jobPost) {
       Pair<String, String> companyServiceResponse = companyService.postJob(jobPost);
       if(companyServiceResponse.getLeft().equals("failed")) {
          return ResponseEntity.badRequest().body(companyServiceResponse.getRight());
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
        log.info("SUMANTA : Inside");
        try {
            List<JobPost> allInactiveJobs = companyService.getAllInactiveJobs(companyId);
            return ResponseEntity.ok(allInactiveJobs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/job-all-applications/{jobId}")
    @PreAuthorize(("hasRole('Company')"))
    public ResponseEntity<List<ApplicationDto>> showAllApplicationsForJobId(@PathVariable("jobId") String jobId) {
        try {
           List<ApplicationDto> applications = companyService.getAllApplicationsForJob(jobId);
           return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PatchMapping("/set-job-status")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<String> setJobbStatus(@RequestBody BasicDto dto) {
        String jobId = dto.getId();
        Boolean status = dto.getStatus();
        if(jobId == null || status == null) {
            return ResponseEntity.badRequest().body("Job ID or Status is missing");
        }
        try {
            companyService.setJobStatus(jobId, status);
            return ResponseEntity.ok("Job status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update job status");
        }
    }

    @PatchMapping("/set-application-status")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<String> setApplicationStatus(@RequestBody BasicDto dto) {
        String applicationId = dto.getId();
        applicationStatus status = dto.getApplicationStatus();
        if(applicationId == null || status == null) {
            return ResponseEntity.badRequest().body("Application ID or Status is missing");
        }
        try {
            companyService.setApplicationStatus(applicationId, status);
            return ResponseEntity.ok("Application status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update application status");
        }
    }

    @GetMapping("/view-all-selected-applications/{jobId}")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<List<Application>> viewAllSelectedApplications(@PathVariable("jobId") String jobId) {
        try {
            List<Application> selectedApplications = companyService.getAllSelectedApplicationsForJob(jobId);
            return ResponseEntity.ok(selectedApplications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
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
    public ResponseEntity<String> deleteJob(@RequestParam("jobId") String jobId, HttpServletRequest request) {
        String serviceResponse = companyService.deleteJob(jobId, request.getHeader("Authorization").substring(7));
        if(serviceResponse.equals("error")) {
            return ResponseEntity.badRequest().body("Job Couldn't be deleted / Job doesn't exist");
        }
        return ResponseEntity.ok("Job deleted successfully");
    }

    @GetMapping("/health-check")
    @PreAuthorize("hasRole('Company')")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Company Service is running");
    }

}
