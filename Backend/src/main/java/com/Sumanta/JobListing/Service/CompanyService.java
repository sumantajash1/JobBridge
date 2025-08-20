package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthRequestBody;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.ApiResponse;
import com.Sumanta.JobListing.Entity.Application;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Entity.applicationStatus;

import java.util.List;

public interface CompanyService {

     ApiResponse<AuthResponseDto> register(Company company);

     ApiResponse<AuthResponseDto> login(AuthRequestBody authRequestBody);

     ApiResponse<String> postJob(JobPost jobPost, String jwtToken);

     ApiResponse<List<JobPost>> getAllActiveJobs(String jwtToken);

     ApiResponse<List<JobPost>> getAllInactiveJobs(String jwtToken);

     ApiResponse<List<ApplicationDto>> getAllApplicationsForJob(String jobId, String jwtToken);

     ApiResponse<Void> setJobStatus(String jobId, Boolean status, String jwtToken);

     ApiResponse<Void> setApplicationStatus(String applicationId, applicationStatus status, String jwtToken);

     ApiResponse<List<Application>> getAllSelectedApplicationsForJob(String jwtToken, String jobId);

     ApiResponse<Void> deleteJob(String jobId, String jwtToken);

     ApiResponse<String> getCompanyName(String gstNum);

     ApiResponse<String> resetPassword(String mobNo, String password);

}
