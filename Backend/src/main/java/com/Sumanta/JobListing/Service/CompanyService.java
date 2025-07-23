package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.ApplicationDao;
import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.CompanyLoginRequestBody;
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

    public Pair<String, String> register(Company company) {
        if(!GstNumberValidator.isGstNumValid(company.getGstNum())) {
            return Pair.of("failed", "InvalidGST");
        }
        String existsResponse = alreadyExists(company);
        if(!existsResponse.equals("NO")) {
            return Pair.of("failed", existsResponse);
        }
        company.setCompanyPassword(passwordEncoder.encode(company.getCompanyPassword()));
        company.setCompanyContactNum("+91"+company.getCompanyContactNum());
        companyDAO.save(company);
        return Pair.of(company.getCompanyName(), JwtTokenUtil.GenerateToken(company.getGstNum(), Role.Company));
    }

    public Pair<String, String> Login(CompanyLoginRequestBody companyLoginRequestBody) {
        String gstNum = companyLoginRequestBody.getGstNum();
        if(!GstNumberValidator.isGstNumValid(gstNum)) {
            return Pair.of("failed", "InvalGstNum");
        }
       if(!companyDAO.existsById(gstNum)) {
            return Pair.of("failed", "NotFound");
        }
        Company tempCompany = companyDAO.findById(gstNum).get();
        if(!passwordEncoder.matches(companyLoginRequestBody.getPassword(), tempCompany.getCompanyPassword())) {
            return Pair.of("failed", "WrongPassword");
        }
        return Pair.of(tempCompany.getCompanyName(), JwtTokenUtil.GenerateToken(gstNum, Role.Company));
    }

    private String alreadyExists(Company company) {
        if (companyDAO.existsByGstNum(company.getGstNum())) {
            return "gstExists";
        }
        if (companyDAO.existsByCompanyName(company.getCompanyName())) {
            return "nameExists";
        }
        if (companyDAO.existsByCompanyContactNum(company.getCompanyContactNum())) {
            return "contactNumberExists";
        }
        if (companyDAO.existsByCompanyEmail(company.getCompanyEmail())) {
            return "emailExists";
        }
        return "NO";
    }

    public Pair<String, String> postJob(JobPost jobPost) {
        if(jobDao.existsByJobTitle(jobPost.getJobTitle())) {
            return Pair.of("failed", "A Job Post with this Title Already Exists, Please consider editing that Job Post");
        }
        jobPost.setApplicants(new ArrayList<>());
        jobDao.save(jobPost);
        return Pair.of("Job ID : ", jobPost.getJobId());
    }

    public String getComapnyName(String userId) {
        Company company = companyDAO.findByGstNum(userId);
        return company.getCompanyName();
    }

    public Boolean resetPassword(String mobNo, String password) {
        try {
            Company company = companyDAO.findByCompanyContactNum(mobNo);
            company.setCompanyPassword(passwordEncoder.encode(password));
            companyDAO.save(company);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<JobPost> getAllActiveJobs(String companyId) {
       return jobDao.findAllByCompanyIdAndActiveStatusTrue(companyId);
    }
    public List<JobPost> getAllInactiveJobs(String companyId) {
        return jobDao.findAllByCompanyIdAndActiveStatusFalse(companyId);
    }

    public List<ApplicationDto> getAllApplicationsForJob(String jobId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("ApplicantData", "mobNo", "mobNo", "applicantDetails"),
                Aggregation.lookup("CompanyData", "gstNum", "gstNum", "companyDetails"),
                Aggregation.unwind("applicantDetails", true),
                Aggregation.unwind("companyDetails", true),
                Aggregation.project("applicationId", "jobId", "applicantId", "companyId", "resumeId", "status")
                        .and("_id").as("applicationId")
                        .and("applicantDetails.name").as("applicantName")
                        .and("companyDetails.companyName").as("companyName")
        );
        return mongoTemplate.aggregate(aggregation, "applications", ApplicationDto.class).getMappedResults();
    }

    public void setJobStatus(String jobId, Boolean status) {
        Optional<JobPost> jobPostOptional = jobDao.findById(jobId);
        if (jobPostOptional.isPresent()) {
            JobPost jobPost = jobPostOptional.get();
            jobPost.setActiveStatus(status);
            jobDao.save(jobPost);
        } else {
            throw new RuntimeException("Job not found with ID: " + jobId);
        }
    }

    public void setApplicationStatus(String applicationId, applicationStatus status) {
        try {
            Optional<Application> OptionalApplication = applicationDao.findById(applicationId);
            if(OptionalApplication.isPresent()) {
                Application application = OptionalApplication.get();
                application.setStatus(status);
                applicationDao.save(application);
            } else {
                throw new RuntimeException("Application not found with ID: " + applicationId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Application> getAllSelectedApplicationsForJob(String jobId) {
        return applicationDao.findAllByJobIdAndStatus(jobId, applicationStatus.SELECTED);
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

    public String deleteJob(String jobId, String jwtToken) {
        if(!jobDao.existsById(jobId)) {
            return "error";
        }
        String companyId = JwtTokenUtil.getUserIdFromToken(jwtToken);
        List<JobPost> jobs = jobDao.findAllByCompanyId(companyId);
        JobPost tempJob = jobDao.findById(jobId).get();
        boolean exists = jobs.stream().anyMatch(job -> job.getJobId().equals(tempJob.getJobId()));
        if(!exists) {
            return "error";
        }
        try {
            jobDao.deleteById(jobId);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "success";
    }
}
