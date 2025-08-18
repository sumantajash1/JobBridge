package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.ApplicantDAO;
import com.Sumanta.JobListing.DAO.ApplicationDao;
import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.DTO.AuthRequestBody;
import com.Sumanta.JobListing.DTO.AuthResponseDto;
import com.Sumanta.JobListing.DTO.ResponseWrapper;
import com.Sumanta.JobListing.Entity.*;
import com.Sumanta.JobListing.Exception.*;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
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
public class ApplicantService {
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
    ResumeService resumeService;
    @Autowired
    CompanyDAO companyDao;

    public ResponseWrapper<AuthResponseDto> register(Applicant applicant) {
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
        return new ResponseWrapper<>(
                true,
                201,
                "New user account has been created successfully",
                new AuthResponseDto(applicant.getName(), jwtToken),
                null
        );
    }

    public ResponseWrapper<AuthResponseDto> logIn(AuthRequestBody applicantLoginRequestBody) {
        String mobNo = applicantLoginRequestBody.getId();
        if(!applicantDAO.existsByMobNo(mobNo)) {
            throw new ApplicantNotFoundException();
        }
        Applicant applicant = applicantDAO.findByMobNo(mobNo);
        if(!passwordEncoder.matches(applicantLoginRequestBody.getPassword(), applicant.getPassword())) {
            throw new InvalidCredentialsException("password");
        }
        String jwtToken = JwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return new ResponseWrapper<AuthResponseDto>(
                true,
                200,
                "User logged in Successfully.",
                new AuthResponseDto(applicant.getName(), jwtToken),
                null
        );
    }

    public ResponseWrapper<List<JobPost>> fetchAllJobs() {
        List<JobPost> jobs = jobDao.findAllByActiveStatusTrue();
        return new ResponseWrapper<>(
                true,
                200,
                "All jobs has been fetched.",
                jobs,
                null
        );
    }

    public ResponseWrapper<Void> resetPassword(String mobNo, String newPassword) {
        Applicant applicant = applicantDAO.findByMobNo(mobNo);
        if(applicant == null) {
            throw new ApplicantNotFoundException();
        }
        applicant.setPassword(passwordEncoder.encode(newPassword));
        applicantDAO.save(applicant);
        return new ResponseWrapper<>(
                true,
                200,
                "Password reset successful.",
                null,
                null
        );
    }

    public ResponseWrapper<Void> applyToJob(String jobId, String jwtToken, String companyId, MultipartFile resume) throws IOException {
            String applicantId = JwtTokenUtil.getUserIdFromToken(jwtToken);
            if(!applicantDAO.existsById(applicantId)) {
               throw new ApplicantNotFoundException();
            }
            Optional<Company> optionalCompany = companyDao.findById(companyId);
            if(optionalCompany.isEmpty()) {
               throw new CompanyNotFoundException();
            }
            Optional<JobPost> optionalJob = jobDao.findById(jobId);
            if(optionalJob.isEmpty() || !optionalJob.get().getCompanyId().equals(companyId)) {
                throw new JobNotFoundException(jobId);
            }
            JobPost job = optionalJob.get();
            List<String> applicantIds = job.getApplicants();
            List<Application> applications = applicationDao.findByApplicantId(applicantId);
            boolean alreadyApplied = applications.stream().anyMatch(application -> application.getJobId().equals(jobId));
            if(alreadyApplied) {
                throw new ApplicantAlreadyAppliedToJobException(jobId);
            }
            Application application = new Application();
            application.setApplicantId(applicantId);
            application.setJobId(jobId);
            application.setCompanyId(companyId);
            application.setResumeId(resumeService.uploadResume(resume));
            application.setStatus(applicationStatus.PENDING);
            applicationDao.save(application);
            applicantIds.add(applicantId);
            jobDao.save(job);
        return new ResponseWrapper<>(
                true,
                201,
                "Applicant successfully applied to this job",
                null,
                null
        );
    }

    public ResponseWrapper<List<ApplicationDto>> getAllApplications(String jwtToken) {
        String applicantId = JwtTokenUtil.getUserIdFromToken(jwtToken);
        List<Application> applications = applicationDao.findByApplicantId(applicantId);
        if (applications.isEmpty()) {
            return new ResponseWrapper<>(false, 204, "No applications found.", null, null);
        }
        Optional<Company> optionalCompany = companyDao.findById(applications.get(0).getCompanyId());
        if (optionalCompany.isEmpty()) {
            throw new CompanyNotFoundException();
        }
        Company company = optionalCompany.get();
        String companyName = company.getCompanyName();
        List<ApplicationDto> dtos = new ArrayList<>();
        for (Application application : applications) {
            ApplicationDto dto = new ApplicationDto(
                    application.getApplicationId(),
                    application.getJobId(),
                    applicantId,
                    application.getCompanyId(),
                    null,
                    companyName,
                    application.getResumeId(),
                    application.getStatus()
            );
            dtos.add(dto);
        }
        return new ResponseWrapper<>(true, 200, "Returning all the applications", dtos, null);
    }

    public ResponseWrapper<Void> deleteAccount(String jwtToken) {
        String applicantId = JwtTokenUtil.getUserIdFromToken(jwtToken);
        if(!applicantDAO.existsById(applicantId)) {
            throw new ApplicantNotFoundException();
        }
        List<Application> applications = applicationDao.findByApplicantId(applicantId);
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
                applicants.remove(applicantId);
                job.setApplicants(applicants);
                jobDao.save(job);
            }
        }
        for(String r : resumeIds) {
            resumeService.deleteFileById(r);
        }
        applicantDAO.deleteById(applicantId);
        return new ResponseWrapper<>(
                true,
                200,
                "Account has been deleted successfully.",
                null,
                null
        );
    }

    public ResponseWrapper<Void> removeApplication(String applicationId, String jwtToken) {
        String applicantId = JwtTokenUtil.getUserIdFromToken(jwtToken);
        Optional<Application> optionalApplication = applicationDao.findByApplicationId(applicationId);
        if(optionalApplication.isEmpty() || !optionalApplication.get().getApplicantId().equals(applicantId)) {
            throw new ApplicationNotFoundException(applicantId);
        }
        String resumeId = optionalApplication.get().getResumeId();
        resumeService.deleteFileById(resumeId);
        Optional<JobPost> job = jobDao.findById(optionalApplication.get().getJobId());
        if(job.isPresent()) {
            List<String> applicantIds = job.get().getApplicants();
            applicantIds.remove(applicantId);
            jobDao.save(job.get());
        }
        applicationDao.deleteById(applicationId);
        return new ResponseWrapper<>(
                true,
                204,
                "Application withdrawn successfully.",
                null,
                null
        );
    }
}

