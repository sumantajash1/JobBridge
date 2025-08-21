package com.Sumanta.JobListing.Service.impl;

import com.Sumanta.JobListing.DAO.ApplicationDao;
import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.AuthRequestBody;
import com.Sumanta.JobListing.DTO.ApiResponse;
import com.Sumanta.JobListing.Entity.*;
import com.Sumanta.JobListing.Entity.enums.Role;
import com.Sumanta.JobListing.Entity.enums.applicationStatus;
import com.Sumanta.JobListing.Exception.*;
import com.Sumanta.JobListing.Service.ApplicantService;
import com.Sumanta.JobListing.Service.CompanyService;
import com.Sumanta.JobListing.utils.GstNumberValidator;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import com.twilio.rest.bulkexports.v1.export.Job;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JobDao jobDao;
    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private ApplicantService applicantService;

    @Override
    public Company getCompanyOrThrow(String gstNum) {
        return companyDAO.findById(gstNum).orElseThrow(() -> new CompanyNotFoundException(gstNum));
    }

    @Override
    public ApiResponse<AuthResponseDto> register(Company company) {
        if(!GstNumberValidator.isGstNumValid(company.getGstNum())) {
            throw new InvalidCredentialsException("gst number");
        }
        Pair<Boolean, String> conflictResponse =  alreadyExists(company);
        if(conflictResponse.getLeft().equals(true)) {
            throw new DuplicateCompanyException(conflictResponse.getRight());
        }
        company.setCompanyPassword(passwordEncoder.encode(company.getCompanyPassword()));
        company.setCompanyContactNum("+91"+company.getCompanyContactNum());
        companyDAO.save(company);
        String jwtToken = JwtTokenUtil.GenerateToken(company.getGstNum(), Role.Company);
        return ApiResponse.created(new AuthResponseDto(company.getCompanyName(), jwtToken), "New User has been created");
    }

    @Override
    public ApiResponse<AuthResponseDto> login(AuthRequestBody authRequestBody) {
        String gstNum = authRequestBody.getId();
        if(!GstNumberValidator.isGstNumValid(gstNum)) {
            throw new InvalidCredentialsException("gst number");
        }
        Company company = getCompanyOrThrow(gstNum);
        if(!passwordEncoder.matches(authRequestBody.getPassword(), company.getCompanyPassword())) {
            throw new InvalidCredentialsException("password");
        }
        String jwtToken = JwtTokenUtil.GenerateToken(gstNum, Role.Company);
        return ApiResponse.ok(new AuthResponseDto(company.getCompanyName(), jwtToken), "Login successful");
    }

    private Pair<Boolean, String> alreadyExists(Company company) {
        if (companyDAO.existsByGstNum(company.getGstNum())) {
            return Pair.of(true, "GST number is already associated with another company.");
        }
        if (companyDAO.existsByCompanyName(company.getCompanyName())) {
            return Pair.of(true, "Company name is already associated with another company.");
        }
        if (companyDAO.existsByCompanyContactNum(company.getCompanyContactNum())) {
            return Pair.of(true, "Contact number is already associated with another company.");
        }
        if (companyDAO.existsByCompanyEmail(company.getCompanyEmail())) {
            return Pair.of(true, "Email ID is already associated with another company.");
        }
        return Pair.of(false, "No conflict found.");
    }

    @Override
    public ApiResponse<String> postJob(JobPost jobPost, String gstNum) {
        Company company = getCompanyOrThrow(gstNum);
        List<JobPost> jobs = jobDao.findByCompanyId(gstNum);
        boolean exists = jobs.stream().anyMatch(j -> j.getJobTitle().equals(jobPost.getJobTitle()));
        if(exists) {
            throw new DuplicateJobException(jobPost.getJobTitle());
        }
        String companyName = company.getCompanyName();
        jobPost.setCompanyId(gstNum);
        jobPost.setCompanyName(companyName);
        jobPost.setApplicants(new ArrayList<>());
        jobDao.save(jobPost);
        return ApiResponse.created(jobPost.getJobId(), "Job posted successfully");
    }

    @Override
    public ApiResponse<List<JobPost>> getAllActiveJobs(String gstNum) {
        Company company = getCompanyOrThrow(gstNum);
        List<JobPost> jobs = jobDao.findAllByCompanyIdAndActiveStatusTrue(gstNum);
        return ApiResponse.ok(jobs, "Active jobs fetched");
    }

    @Override
    public ApiResponse<List<JobPost>> getAllInactiveJobs(String gstNum) {
        Company company = getCompanyOrThrow(gstNum);
        return ApiResponse.ok(jobDao.findAllByCompanyIdAndActiveStatusFalse(gstNum), "All inactive jobs fetched");
    }

    @Override
    public ApiResponse<List<ApplicationDto>> getAllApplicationsForJob(String jobId, String gstNum) {
        Company company = getCompanyOrThrow(gstNum);
        JobPost job = applicantService.getJobOrThrow(jobId, gstNum);
        List<Application> applications = applicationDao.findAllByJobId(jobId);
        if(applications.isEmpty()) {
            return ApiResponse.noContent();
        }
        List<ApplicationDto> dtos = new ArrayList<>();
        for(Application application : applications) {
            ApplicationDto dto = new ApplicationDto();
            Applicant applicant = applicantService.getApplicantOrThrow(application.getApplicantId());
            String applicantName = applicant.getName();
            dto.setApplicationId(application.getApplicationId());
            dto.setJobId(application.getJobId());
            dto.setApplicantId(application.getApplicantId());
            dto.setCompanyId(gstNum);
            dto.setApplicantName(applicantName);
            dto.setResumeId(application.getResumeId());
            dto.setStatus(application.getStatus());
            dtos.add(dto);
        }
        return ApiResponse.ok(dtos, "Returning all the applications for this job.");
    }

    @Override
    public ApiResponse<Void> setJobStatus(String jobId, Boolean status, String gstNum) {
        Company company = getCompanyOrThrow(gstNum);
        List<JobPost> jobs = jobDao.findByCompanyId(gstNum);
        boolean exists = jobs.stream().anyMatch(job -> job.getJobId().equals(jobId));
        if (exists) {
            JobPost jobPost = jobDao.findById(jobId).get();
            jobPost.setActiveStatus(status);
            jobDao.save(jobPost);
            return ApiResponse.noContent();
        } else {
            throw new JobNotFoundException(jobId);
        }
    }

    @Override
    public ApiResponse<Void> setApplicationStatus(String applicationId, applicationStatus status, String gstNum) {
        Company company = getCompanyOrThrow(gstNum);
        Optional<Application> OptionalApplication = applicationDao.findById(applicationId);
        if(OptionalApplication.isPresent()) {
            Application application = OptionalApplication.get();
            application.setStatus(status);
            applicationDao.save(application);
            return ApiResponse.noContent();
        } else {
            throw new ApplicationNotFoundException(applicationId);
        }
    }

    @Override
    public ApiResponse<List<Application>> getAllSelectedApplicationsForJob(String gstNum, String jobId) {
        Company company = getCompanyOrThrow(gstNum);
        List<JobPost> jobs = jobDao.findByCompanyId(gstNum);
        JobPost job = applicantService.getJobOrThrow(jobId, gstNum);
        List<Application> selected = new ArrayList<>();
        for(String applicationId : job.getApplicants()) {
            Optional<Application> optionalApplication = applicationDao.findById(applicationId);
            if(optionalApplication.isEmpty()) {
                continue;
            }
            Application application  = optionalApplication.get();
            if(application.getStatus().equals(applicationStatus.SELECTED)) {
                selected.add(application);
            }
        }
        return ApiResponse.ok(selected, "Selected applications fetched");
    }


    @Scheduled(cron = "0 0 1 * * *")
    public void automaticDeactivationOfJobs() {
        List<JobPost> jobs = jobDao.findAllByActiveStatusTrueAndDeadlineBefore(LocalDate.now());
        if(jobs.isEmpty()) {
            return;
        }
        jobs.stream().forEach(job -> {
                job.setActiveStatus(false);
                jobDao.save(job);
            }
        );
    }

    @Override
    public ApiResponse<Void> deleteJob(String jobId, String gstNum) {
        Company company = getCompanyOrThrow(gstNum);
        JobPost job = applicantService.getJobOrThrow(jobId, gstNum);
        jobDao.deleteById(jobId);
        return ApiResponse.noContent();
    }

    @Override
    public ApiResponse<String> getCompanyName(String gstNum) {
        Company company = companyDAO.findByGstNum(gstNum);
        if(company == null) {
            throw new CompanyNotFoundException(gstNum);
        }
        return ApiResponse.ok(company.getCompanyName(), "Company name fetched");
    }

    @Override
    public ApiResponse<String> resetPassword(String mobNo, String password) {
        Company company = companyDAO.findByCompanyContactNum(mobNo);
        if(company == null) {
            throw new CompanyNotFoundException(company.getGstNum());
        }
        company.setCompanyPassword(passwordEncoder.encode(password));
        companyDAO.save(company);
        return ApiResponse.noContent();
    }
}
