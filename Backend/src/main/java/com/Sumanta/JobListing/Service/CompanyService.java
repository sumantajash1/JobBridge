package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.ApplicantDAO;
import com.Sumanta.JobListing.DAO.ApplicationDao;
import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.AuthRequestBody;
import com.Sumanta.JobListing.DTO.ResponseWrapper;
import com.Sumanta.JobListing.Entity.*;
import com.Sumanta.JobListing.Exception.*;
import com.Sumanta.JobListing.utils.GstNumberValidator;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    private ApplicantDAO applicantDao;

    public ResponseWrapper<AuthResponseDto> register(Company company) {
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
        return new ResponseWrapper<>(
                true,
                201,
                "New user has been created.",
                new AuthResponseDto(company.getCompanyName(), jwtToken),
                null
        );
    }

    public ResponseWrapper<AuthResponseDto> login(AuthRequestBody authRequestBody) {
        String gstNum = authRequestBody.getId();
        if(!GstNumberValidator.isGstNumValid(gstNum)) {
            throw new InvalidCredentialsException("gst number");
        }
        Optional<Company> optionalCompany = companyDAO.findById(gstNum);
        if(optionalCompany.isEmpty()) {
            throw new CompanyNotFoundException();
        }
        Company company = optionalCompany.get();
        if(!passwordEncoder.matches(authRequestBody.getPassword(), company.getCompanyPassword())) {
            throw new InvalidCredentialsException("password");
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

    public ResponseWrapper<String> postJob(JobPost jobPost, String jwtToken) {
        String gstNum = JwtTokenUtil.getUserIdFromToken(jwtToken);
        Optional<Company> optionalCompany = companyDAO.findById(gstNum);
        if(optionalCompany.isEmpty()) {
            throw new CompanyNotFoundException();
        }
        List<JobPost> jobs = jobDao.findByCompanyId(gstNum);
        boolean exists = jobs.stream().anyMatch(j -> j.getJobTitle().equals(jobPost.getJobTitle()));
        if(exists) {
            throw new DuplicateJobException(jobPost.getJobTitle());
        }
        String companyName = optionalCompany.get().getCompanyName();
        jobPost.setCompanyId(gstNum);
        jobPost.setCompanyName(companyName);
        jobPost.setApplicants(new ArrayList<>());
        jobDao.save(jobPost);
        return new ResponseWrapper<>(true, 201, "Job posted successfully", jobPost.getJobId(), null);
    }

    public ResponseWrapper<List<JobPost>> getAllActiveJobs(String jwtToken) {
        String gstNum = JwtTokenUtil.getUserIdFromToken(jwtToken);
        Optional<Company> optionalCompany = companyDAO.findById(gstNum);
        if(optionalCompany.isEmpty()) {
            throw new CompanyNotFoundException();
        }
        List<JobPost> jobs = jobDao.findAllByCompanyIdAndActiveStatusTrue(gstNum);
        return new ResponseWrapper<>(true, 200, "Active jobs fetched", jobs, null);
    }

    public ResponseWrapper<List<JobPost>> getAllInactiveJobs(String jwtToken) {
        String gstNum = JwtTokenUtil.getUserIdFromToken(jwtToken);
        Optional<Company> optionalCompany = companyDAO.findById(gstNum);
        if(optionalCompany.isEmpty()) {
            throw new CompanyNotFoundException();
        }
        return new ResponseWrapper<>(
                true,
                200,
                "All inactive jobs fetched.",
                jobDao.findAllByCompanyIdAndActiveStatusFalse(gstNum),
                null
        );
    }

    public ResponseWrapper<List<ApplicationDto>> getAllApplicationsForJob(String jobId, String jwtToken) {
        String gstNum = JwtTokenUtil.getUserIdFromToken(jwtToken);
        Optional<Company> optionalCompany = companyDAO.findById(gstNum);
        if(optionalCompany.isEmpty()) {
            throw new CompanyNotFoundException();
        }
        Optional<JobPost> optionalJob = jobDao.findById(jobId);
        if(optionalJob.isEmpty() || !optionalJob.get().getCompanyId().equals(gstNum)) {
            throw new JobNotFoundException(jobId);
        }
        List<Application> applications = applicationDao.findAllByJobId(jobId);
        if(applications.isEmpty()) {
            return new ResponseWrapper<>(false, 204, "No application found for this job.", null, null);
        }
        List<ApplicationDto> dtos = new ArrayList<>();
        for(Application application : applications) {
            ApplicationDto dto = new ApplicationDto();
            Optional<Applicant> optionalApplicant = applicantDao.findById(application.getApplicantId());
            if(optionalApplicant.isEmpty()) {
                continue;
            }
            String applicantName = optionalApplicant.get().getName();
            dto.setApplicationId(application.getApplicationId());
            dto.setJobId(application.getJobId());
            dto.setApplicantId(application.getApplicantId());
            dto.setCompanyId(gstNum);
            dto.setApplicantName(applicantName);
            dto.setResumeId(application.getResumeId());
            dto.setStatus(application.getStatus());
            dtos.add(dto);
        }
        return new ResponseWrapper<>(true, 200, "Returning all the applications for this job.", dtos, null);
    }

    public ResponseWrapper<Void> setJobStatus(String jobId, Boolean status, String jwtToken) {
        String gstNum = JwtTokenUtil.getUserIdFromToken(jwtToken);
        if(!companyDAO.existsByGstNum(gstNum)) {
           throw new CompanyNotFoundException();
        }
        List<JobPost> jobs = jobDao.findByCompanyId(gstNum);
        boolean exists = jobs.stream().anyMatch(job -> job.getJobId().equals(jobId));
        if (exists) {
            JobPost jobPost = jobDao.findById(jobId).get();
            jobPost.setActiveStatus(status);
            jobDao.save(jobPost);
            return new ResponseWrapper<>(true, 200, "Job status updated successfully", null, null);
        } else {
            throw new JobNotFoundException(jobId);
        }
    }

    public ResponseWrapper<Void> setApplicationStatus(String applicationId, applicationStatus status, String jwtToken) {
        String gstNum = JwtTokenUtil.getUserIdFromToken(jwtToken);
        if(!companyDAO.existsByGstNum(gstNum)) {
            throw new CompanyNotFoundException();
        }
        Optional<Application> OptionalApplication = applicationDao.findById(applicationId);
        if(OptionalApplication.isPresent()) {
            Application application = OptionalApplication.get();
            application.setStatus(status);
            applicationDao.save(application);
            return new ResponseWrapper<>(true, 200, "Application Status is set to required.", null, null);
        } else {
            throw new ApplicationNotFoundException(applicationId);
        }
    }

    public ResponseWrapper<List<Application>> getAllSelectedApplicationsForJob(String jwtToken, String jobId) {
        String gstNum = JwtTokenUtil.getUserIdFromToken(jwtToken);
        if(!companyDAO.existsByGstNum(gstNum)) {
            throw new CompanyNotFoundException();
        }
        List<JobPost> jobs = jobDao.findByCompanyId(gstNum);
        JobPost job = null;
        for(JobPost j : jobs) {
            if(j.getJobId().equals(jobId)) {
                job = j;
            }
        }
        if(job == null) {
            throw new JobNotFoundException(jobId);
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

    public ResponseWrapper<Void> deleteJob(String jobId, String jwtToken) {
        String gstNum = JwtTokenUtil.getUserIdFromToken(jwtToken);
        if(!companyDAO.existsByGstNum(gstNum)) {
            throw new CompanyNotFoundException();
        }
        List<JobPost> jobs = jobDao.findAllByCompanyId(gstNum);
        boolean exists = jobs.stream().anyMatch(job -> job.getJobId().equals(jobId));
        if(!exists) {
            throw new JobNotFoundException(jobId);
        }
        jobDao.deleteById(jobId);
        return new ResponseWrapper<>(true, 200, "Job deleted successfully.", null, null);
    }


    public ResponseWrapper<String> getComapnyName(String gstNum) {
        Company company = companyDAO.findByGstNum(gstNum);
        if(company == null) {
            throw new CompanyNotFoundException();
        }
        return new ResponseWrapper<>(true, 200, "Company name fetched", company.getCompanyName(), null);
    }


    public ResponseWrapper<String> resetPassword(String mobNo, String password) {
        Company company = companyDAO.findByCompanyContactNum(mobNo);
        if(company == null) {
            throw new CompanyNotFoundException();
        }
        company.setCompanyPassword(passwordEncoder.encode(password));
        companyDAO.save(company);
        return new ResponseWrapper<>(true, 200, "Password reset successful", null, null);
    }

}
