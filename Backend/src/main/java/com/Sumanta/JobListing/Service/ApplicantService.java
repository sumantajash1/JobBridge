package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthRequestBody;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.ResponseWrapper;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.JobPost;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ApplicantService {

     ResponseWrapper<AuthResponseDto> register(Applicant applicant);

     ResponseWrapper<AuthResponseDto> logIn(AuthRequestBody applicantLoginRequestBody);

     ResponseWrapper<List<JobPost>> fetchAllJobs();

     ResponseWrapper<Void> resetPassword(String mobNo, String newPassword);

     ResponseWrapper<Void> applyToJob(String jobId, String jwtToken, String companyId, MultipartFile resume) throws IOException;

     ResponseWrapper<List<ApplicationDto>> getAllApplications(String jwtToken);

     ResponseWrapper<Void> deleteAccount(String jwtToken);

     ResponseWrapper<Void> removeApplication(String applicationId, String jwtToken);
}
