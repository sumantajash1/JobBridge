package com.Sumanta.JobListing.Service.impl;

import com.Sumanta.JobListing.DAO.ApplicantDAO;
import com.Sumanta.JobListing.DAO.ApplicationDao;
import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthRequestBody;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.ApiResponse;
import com.Sumanta.JobListing.Entity.*;
import com.Sumanta.JobListing.Entity.enums.Role;
import com.Sumanta.JobListing.Entity.enums.applicationStatus;
import com.Sumanta.JobListing.Exception.*;
import com.Sumanta.JobListing.Service.ApplicantService;
import com.Sumanta.JobListing.Service.CompanyService;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import com.twilio.rest.bulkexports.v1.export.Job;
import com.twilio.rest.microvisor.v1.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicantServiceImpl implements ApplicantService {
    @Autowired
    ApplicantDAO applicantDAO;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JobDao jobDao;
    @Autowired
    ApplicationDao applicationDao;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    ResumeServiceImpl resumeService;
    @Autowired
    CompanyService companyService;

    @Override
    public JobPost getJobOrThrow(String jobId, String gstNum) {
        return jobDao.findById(jobId).filter(job -> job.getCompanyId().equals(gstNum)).orElseThrow(() -> new JobNotFoundException(jobId));
    }

    @Override
    public Applicant getApplicantOrThrow(String mobileNum) {
        return applicantDAO.findById(mobileNum).orElseThrow(() -> new ApplicantNotFoundException());
    }

    @Override
    public ApiResponse<AuthResponseDto> register(Applicant applicant) {
        String mobNo = applicant.getMobNo();
        if(applicantDAO.existsByMobNo(mobNo)) {
            throw new DuplicateApplicantException("mobile number");
        }
        if(applicantDAO.existsByEmail(applicant.getEmail())) {
            throw new DuplicateApplicantException("email id");
        }
        applicant.setPassword(passwordEncoder.encode(applicant.getPassword()));
        applicantDAO.save(applicant);
        String jwtToken = JwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return ApiResponse.created(new AuthResponseDto(applicant.getName(), jwtToken), "New user account has been created successfully");
    }

    @Override
    public ApiResponse<AuthResponseDto> logIn(AuthRequestBody applicantLoginRequestBody) {
        String mobNo = applicantLoginRequestBody.getId();
       Applicant applicant = getApplicantOrThrow(mobNo);
        if(!passwordEncoder.matches(applicantLoginRequestBody.getPassword(), applicant.getPassword())) {
            throw new InvalidCredentialsException("password");
        }
        String jwtToken = JwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return ApiResponse.ok(new AuthResponseDto(applicant.getName(), jwtToken), "User logged in Successfully");
    }

    @Override
    public ApiResponse<List<JobPost>> fetchAllJobs() {
        List<JobPost> jobs = jobDao.findAllByActiveStatusTrue();
        return ApiResponse.ok(jobs, "All jobs has been fetched");
    }

    @Override
    public ApiResponse<Void> resetPassword(String mobNo, String newPassword) {
        Applicant applicant = getApplicantOrThrow(mobNo);
        applicant.setPassword(passwordEncoder.encode(newPassword));
        applicantDAO.save(applicant);
        return ApiResponse.noContent();
    }

    @Override
    public ApiResponse<Void> applyToJob(String jobId, String mobileNum, String companyId, MultipartFile resume) throws IOException {
            Applicant applicant = getApplicantOrThrow(mobileNum);
            Company company = companyService.getCompanyOrThrow(companyId);
            JobPost job = getJobOrThrow(jobId, companyId);
            List<String> applicantIds = job.getApplicants();
            List<Application> applications = applicationDao.findByApplicantId(mobileNum);
            boolean alreadyApplied = applications.stream().anyMatch(application -> application.getJobId().equals(jobId));
            if(alreadyApplied) {
                throw new ApplicantAlreadyAppliedToJobException(jobId);
            }
            Application application = new Application();
            application.setApplicantId(mobileNum);
            application.setJobId(jobId);
            application.setCompanyId(companyId);
            application.setResumeId(resumeService.uploadResume(resume));
            application.setStatus(applicationStatus.PENDING);
            applicationDao.save(application);
            applicantIds.add(mobileNum);
            jobDao.save(job);
            return ApiResponse.noContent();
    }

    @Override
    public ApiResponse<List<ApplicationDto>> getAllApplications(String mobileNum) {
        List<Application> applications = applicationDao.findByApplicantId(mobileNum);
        if (applications.isEmpty()) {
            return ApiResponse.noContent();
        }
        String applicantName = getApplicantOrThrow(mobileNum).getName();
        List<ApplicationDto> dtos = new ArrayList<>();
        for (Application application : applications) {
            Company company = companyService.getCompanyOrThrow(application.getCompanyId());
            String companyName = company.getCompanyName();
            ApplicationDto dto = new ApplicationDto(
                    application.getApplicationId(),
                    application.getJobId(),
                    mobileNum,
                    application.getCompanyId(),
                    applicantName,
                    companyName,
                    application.getResumeId(),
                    application.getStatus()
            );
            dtos.add(dto);
        }
        return ApiResponse.ok(dtos, "All applications are fetched");
    }

    @Override
    public ApiResponse<Void> deleteAccount(String mobileNum) {
        Applicant applicant = getApplicantOrThrow(mobileNum);
        List<Application> applications = applicationDao.findByApplicantId(mobileNum);
        List<String> jobIds = new ArrayList<>();
        List<String> resumeIds = new ArrayList<>();
        for(Application a : applications) {
            jobIds.add(a.getJobId());
            resumeIds.add(a.getResumeId());
            applicationDao.deleteById(a.getApplicationId());
        }
        for(String j : jobIds) {
            Optional<JobPost> optionalJob = jobDao.findById(j);
            if(optionalJob.isPresent()) {
                JobPost job = optionalJob.get();
                List<String> applicants = job.getApplicants();
                applicants.remove(mobileNum);
                job.setApplicants(applicants);
                jobDao.save(job);
            }
        }
        for(String r : resumeIds) {
            resumeService.deleteFileById(r);
        }
        applicantDAO.deleteById(mobileNum);
        return ApiResponse.noContent();
    }

    @Override
    public ApiResponse<Void> removeApplication(String applicationId, String applicantId) {
        Applicant applicant = getApplicantOrThrow(applicantId);
        Optional<Application> optionalApplication = applicationDao.findByApplicationId(applicationId);
        if(optionalApplication.isEmpty() || !optionalApplication.get().getApplicantId().equals(applicantId)) {
            throw new ApplicationNotFoundException(applicantId);
        }
        Application application = optionalApplication.get();
        String resumeId = optionalApplication.get().getResumeId();
        resumeService.deleteFileById(resumeId);
        JobPost job = getJobOrThrow(application.getJobId(), application.getCompanyId());
        List<String> applicantIds = job.getApplicants();
        applicantIds.remove(applicantId);
        jobDao.save(job);
        applicationDao.deleteById(applicationId);
        return ApiResponse.noContent();
    }
}

