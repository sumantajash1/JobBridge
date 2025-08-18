package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthRequestBody;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.ResponseWrapper;
import com.Sumanta.JobListing.Entity.Application;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Entity.applicationStatus;

import java.util.List;

public interface CompanyService {

     ResponseWrapper<AuthResponseDto> register(Company company);

     ResponseWrapper<AuthResponseDto> login(AuthRequestBody authRequestBody);

     ResponseWrapper<String> postJob(JobPost jobPost, String jwtToken);

     ResponseWrapper<List<JobPost>> getAllActiveJobs(String jwtToken);

     ResponseWrapper<List<JobPost>> getAllInactiveJobs(String jwtToken);

     ResponseWrapper<List<ApplicationDto>> getAllApplicationsForJob(String jobId, String jwtToken);

     ResponseWrapper<Void> setJobStatus(String jobId, Boolean status, String jwtToken);

     ResponseWrapper<Void> setApplicationStatus(String applicationId, applicationStatus status, String jwtToken);

     ResponseWrapper<List<Application>> getAllSelectedApplicationsForJob(String jwtToken, String jobId);

     ResponseWrapper<Void> deleteJob(String jobId, String jwtToken);

     ResponseWrapper<String> getCompanyName(String gstNum);

     ResponseWrapper<String> resetPassword(String mobNo, String password);

}
