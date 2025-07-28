package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.ApplicationDao;
import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.CompanyLoginRequestBody;
import com.Sumanta.JobListing.DTO.ResponseWrapper;
import com.Sumanta.JobListing.Entity.*;
import com.Sumanta.JobListing.utils.GstNumberValidator;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import com.twilio.rest.bulkexports.v1.export.Job;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JobDao jobDao;
    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    public ResponseWrapper<AuthResponseDto> register(Company company) {
        try {
            if(!GstNumberValidator.isGstNumValid(company.getGstNum())) {
                return new ResponseWrapper(
                        false,
                        401,
                        "GST number is invalid",
                        null,
                        null
                );
            }
            Pair<Boolean, String> conflictResponse =  alreadyExists(company);
            if(conflictResponse.getLeft().equals(true)) {
                return new ResponseWrapper(
                        false,
                        409,
                        conflictResponse.getRight(),
                        null,
                        null
                );
            }
            company.setCompanyPassword(passwordEncoder.encode(company.getCompanyPassword()));
            company.setCompanyContactNum("+91"+company.getCompanyContactNum());
            companyDAO.save(company);
        } catch (Exception e) {
            return new ResponseWrapper(
                    false,
                    409,
                    "Failed due to some unknown server error.",
                    null,
                    e.getMessage()
            );
        }
        String jwtToken = JwtTokenUtil.GenerateToken(company.getGstNum(), Role.Company);
        return new ResponseWrapper<AuthResponseDto>(
                true,
                201,
                "New user has been created.",
                new AuthResponseDto(company.getCompanyName(), jwtToken),
                null
        );
    }

    public ResponseWrapper<AuthResponseDto> login(CompanyLoginRequestBody companyLoginRequestBody) {
        String gstNum = companyLoginRequestBody.getGstNum();
        if(!GstNumberValidator.isGstNumValid(gstNum)) {
            return new ResponseWrapper<>(false, 400, "Invalid GST Number", null, null);
        }
        Optional<Company> optionalCompany = companyDAO.findById(gstNum);
        if(optionalCompany.isEmpty()) {
            return new ResponseWrapper<>(false, 404, "Company not found", null, null);
        }
        Company company = optionalCompany.get();
        if(!passwordEncoder.matches(companyLoginRequestBody.getPassword(), company.getCompanyPassword())) {
            return new ResponseWrapper<>(false, 401, "Wrong Password", null, null);
        }
        String jwtToken = JwtTokenUtil.GenerateToken(gstNum, Role.Company);
        return new ResponseWrapper<>(true, 200, "Login successful", new AuthResponseDto(company.getCompanyName(), jwtToken), null);
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
    public ResponseWrapper<String> postJob(JobPost jobPost) {
        if(jobDao.existsByJobTitle(jobPost.getJobTitle())) {
            return new ResponseWrapper<>(false, 409, "A Job Post with this Title Already Exists, Please consider editing that Job Post", null, null);
        }
        jobPost.setApplicants(new ArrayList<>());
        jobDao.save(jobPost);
        return new ResponseWrapper<>(true, 201, "Job posted successfully", jobPost.getJobId(), null);
    }

   public ResponseWrapper<List<JobPost>> getAllActiveJobsWrapped(String companyId) {
        try {
            List<JobPost> jobs = jobDao.findAllByCompanyIdAndActiveStatusTrue(companyId);
            return new ResponseWrapper<>(true, 200, "Active jobs fetched", jobs, null);
        } catch (Exception e) {
            return new ResponseWrapper<>(false, 500, "Error fetching active jobs", null, e.getMessage());
        }
    }

    public ResponseWrapper<List<JobPost>> getAllInactiveJobs(String companyId) {
        try {
            return new ResponseWrapper(
                    true,
                    200,
                    "All inactive jobs fetched.",
                    jobDao.findAllByCompanyIdAndActiveStatusFalse(companyId),
                    null
            );
        } catch (Exception e) {
            return new ResponseWrapper<>(
                    false,
                    503,
                    "Couldn't be fetched due to unknown server error.",
                    null,
                    e.getMessage()
            );
        }
    }

//    public List<ApplicationDto> getAllApplicationsForJob(String jobId) {
//        Aggregation aggregation = Aggregation.newAggregation(
//                Aggregation.lookup("ApplicantData", "mobNo", "mobNo", "applicantDetails"),
//                Aggregation.lookup("CompanyData", "gstNum", "gstNum", "companyDetails"),
//                Aggregation.unwind("applicantDetails", true),
//                Aggregation.unwind("companyDetails", true),
//                Aggregation.project("applicationId", "jobId", "applicantId", "companyId", "resumeId", "status")
//                        .and("_id").as("applicationId")
//                        .and("applicantDetails.name").as("applicantName")
//                        .and("companyDetails.companyName").as("companyName")
//        );
//        return mongoTemplate.aggregate(aggregation, "applications", ApplicationDto.class).getMappedResults();
//    }

    public ResponseWrapper setJobStatus(String jobId, Boolean status) {
        try {
            Optional<JobPost> jobPostOptional = jobDao.findById(jobId);
            if (jobPostOptional.isPresent()) {
                JobPost jobPost = jobPostOptional.get();
                jobPost.setActiveStatus(status);
                jobDao.save(jobPost);
            } else {
                return new ResponseWrapper<>(false, 404, "No Job to be found with given job Id.", null, null);
            }
            return new ResponseWrapper<>(true, 200, "Job status updated successfully", null, null);
        } catch (Exception e) {
            return new ResponseWrapper<>(false, 500, "Failed to update job status", null, e.getMessage());
        }
    }

    public ResponseWrapper setApplicationStatus(String applicationId, applicationStatus status) {
       try {
            Optional<Application> OptionalApplication = applicationDao.findById(applicationId);
            if(OptionalApplication.isPresent()) {
                Application application = OptionalApplication.get();
                application.setStatus(status);
                applicationDao.save(application);
                return new ResponseWrapper<>(true, 200, "Application Status is set to required.", null, null);
            } else {
                return new ResponseWrapper<>(false, 404, "Application is not found.", null, null);
            }
        } catch (Exception e) {
           return new ResponseWrapper<>(false, 500, "Operation failed due to unknown server error.", null, e.getMessage());
        }
    }

    public ResponseWrapper<List<Application>> getAllSelectedApplicationsForJob(String jwtToken, String jobId) {
        String gstNum = JwtTokenUtil.getUserIdFromToken(jwtToken);
        try {
            List<JobPost> jobs = jobDao.findByCompanyId(gstNum);
            JobPost job = null;
            for(JobPost j : jobs) {
                if(j.getJobId().equals(jobId)) {
                    job = j;
                }
            }
            if(job == null) {
                return new ResponseWrapper<>(false, 404, "Job not found.", null, null);
            }
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
            return new ResponseWrapper<>(true, 200, "Selected applications fetched", selected, null);
        } catch (Exception e) {
            return new ResponseWrapper<>(false, 500, "Error fetching selected applications", null, e.getMessage());
        }
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

    public ResponseWrapper deleteJob(String jobId, String jwtToken) {
        try {
            String companyId = JwtTokenUtil.getUserIdFromToken(jwtToken);
            List<JobPost> jobs = jobDao.findAllByCompanyId(companyId);
            boolean exists = jobs.stream().anyMatch(job -> job.getJobId().equals(jobId));
            if(!exists) {
                return new ResponseWrapper<>(false, 404, "Job not found", null, null);
            }
            jobDao.deleteById(jobId);
            return new ResponseWrapper<>(true, 200, "Job deleted successfully.", null, null);
        } catch (Exception e) {
            return new ResponseWrapper<>(false, 503, "Operation failed due to unknown server error.", null, e.getMessage());
        }
    }

   public ResponseWrapper<String> getComapnyName(String gstNum) {
        try {
            Company company = companyDAO.findByGstNum(gstNum);
            if(company == null) {
                return new ResponseWrapper<>(false, 404, "Company not found", null, null);
            }
            return new ResponseWrapper<>(true, 200, "Company name fetched", company.getCompanyName(), null);
        } catch (Exception e) {
            return new ResponseWrapper<>(false, 500, "Error fetching company name", null, e.getMessage());
        }
    }

   public ResponseWrapper<String> resetPassword(String mobNo, String password) {
        try {
            Company company = companyDAO.findByCompanyContactNum(mobNo);
            if(company == null) {
                return new ResponseWrapper<>(false, 404, "Company not found", null, null);
            }
            company.setCompanyPassword(passwordEncoder.encode(password));
            companyDAO.save(company);
            return new ResponseWrapper<>(true, 200, "Password reset successful", null, null);
        } catch (Exception e) {
            return new ResponseWrapper<>(false, 500, "Password reset failed", null, e.getMessage());
        }
    }

}
