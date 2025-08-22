package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthRequestBody;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.ApiResponse;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.JobPost;
import com.twilio.rest.microvisor.v1.App;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ApplicantService {

     ApiResponse<AuthResponseDto> register(Applicant applicant);

     ApiResponse<AuthResponseDto> logIn(AuthRequestBody applicantLoginRequestBody);

     ApiResponse<List<JobPost>> fetchAllJobs();

     ApiResponse<Void> resetPassword(String mobNo, String newPassword);

     ApiResponse<Void> applyToJob(String jobId, String jwtToken, String companyId, MultipartFile resume) throws IOException;

     ApiResponse<List<ApplicationDto>> getAllApplications(String jwtToken);

     ApiResponse<Void> deleteAccount(String jwtToken);

     ApiResponse<Void> removeApplication(String applicationId, String jwtToken);
}
