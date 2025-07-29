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
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import com.twilio.jwt.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
            return new ResponseWrapper(
                    false,
                    409,
                    "User's mobile number is already associated with another account.",
                    null,
                    null
            );
        }
        if(applicantDAO.existsByEmail(applicant.getEmail())) {
            return new ResponseWrapper(
                    false,
                    409,
                    "User's email id is already associated with another account",
                    null,
                    null
            );
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
            return new ResponseWrapper(
                    false,
                    404,
                    "No user to be found with this mobile number.",
                    null,
                    null
            );
        }
        Applicant applicant = applicantDAO.findByMobNo(mobNo);
        if(!passwordEncoder.matches(applicantLoginRequestBody.getPassword(), applicant.getPassword())) {
            return new ResponseWrapper(
                    false,
                    401,
                    "Wrong Password / Invalid Credentials.",
                    null,
                    null
            );
        }
        String jwtToken = JwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return new ResponseWrapper(
                true,
                200,
                "User logged in Successfully.",
                new AuthResponseDto(applicant.getName(), jwtToken),
                null
        );
    }

    public ResponseWrapper<List<JobPost>> fetchAllJobs() {
        List<JobPost> jobs;
        try {
            jobs = jobDao.findAllByActiveStatusTrue();
        } catch (Exception e) {
            return new ResponseWrapper<>(
                    false,
                    503,
                    "Jobs couldn't be fetched from the database.",
                    null,
                    e.getMessage()
            );
        }
        return new ResponseWrapper<>(
                true,
                200,
                "All jobs has been fetched.",
                jobs,
                null
        );
    }

    public ResponseWrapper resetPassword(String mobNo, String newPassword) {
        try {
            Applicant applicant = applicantDAO.findByMobNo(mobNo);
            if(applicant.equals(null)) {
                return new ResponseWrapper(
                        false,
                        404,
                        "User couldn't be found",
                        null,
                        null
                );
            }
            applicant.setPassword(passwordEncoder.encode(newPassword));
            applicantDAO.save(applicant);
        } catch (Exception e) {
           return new ResponseWrapper(
                   false,
                   503,
                   null,
                   null,
                   e.getMessage()
           );
        }
        return new ResponseWrapper(
                true,
                200,
                "Password reset successful.",
                null,
                null
        );
    }

    public ResponseWrapper applyToJob(String jobId, String applicantId, String companyId, MultipartFile resume) {
        try {
            if(!applicantDAO.existsById(applicantId)) {
                return new ResponseWrapper(
                        false,
                        404,
                        "User not found.",
                        null,
                        null
                );
            }
            Optional<Company> optionalCompany = companyDao.findById(companyId);
            if(optionalCompany.isEmpty()) {
               return new ResponseWrapper(false, 404, "Company not found", null, null);
            }
            Optional<JobPost> optionalJob = jobDao.findById(jobId);
            if(optionalJob.isEmpty() || !optionalJob.get().getCompanyId().equals(companyId)) {
                return new ResponseWrapper(false, 404, "Job not found", null, null);
            }
            JobPost job = optionalJob.get();
            List<String> applicantIds = job.getApplicants();
            List<Application> applications = applicationDao.findByApplicantId(applicantId);
            boolean alreadyApplied = applications.stream().anyMatch(application -> application.getJobId().equals(jobId));
            if(alreadyApplied) {
                return new ResponseWrapper(
                        false,
                        409,
                        "Applicant has already applied to this Job",
                        null,
                        null
                );
            }
            //Application application = new Application(null, applicantId, jobId, companyId, resumeId, applicationStatus.PENDING);
            Application application = new Application();
            application.setApplicantId(applicantId);
            application.setJobId(jobId);
            application.setCompanyId(companyId);
            application.setResumeId(resumeService.uploadResume(resume));
            application.setStatus(applicationStatus.PENDING);
            applicationDao.save(application);
            applicantIds.add(applicantId);
            jobDao.save(job);
        } catch (Exception e) {
            return new ResponseWrapper(
                    false,
                    503,
                    "Couldn't be applied to this job.",
                    null,
                    e.getMessage()
            );
        }
        return new ResponseWrapper(
                true,
                201,
                "Applicant successfully applied to this job",
                null,
                null
        );
    }

    public ResponseWrapper<List<ApplicationDto>> getAllApplications(String jwtToken) {
        String applicantId = JwtTokenUtil.getUserIdFromToken(jwtToken);
        try {
            if(!applicantDAO.existsByMobNo(applicantId)) {
                return new ResponseWrapper(
                        false,
                        404,
                        "User not found.",
                        null,
                        null
                );
            }
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(Criteria.where("applicantId").is(applicantId)),
                    Aggregation.lookup("ApplicantData", "mobNO", "mobNo", "applicantDetails"),
                    Aggregation.lookup("CompanyData", "gstNum", "gstNum", "CompanyDetails"),
                    Aggregation.unwind("applicantDetails", true),
                    Aggregation.unwind("CompanyDetails", true),
                    Aggregation.project("applicationId", "jobId", "applicantId", "companyId", "resumeId", "status")
                            .and("_id").as("applicationId")
                            .and("applicantDetails.name").as("applicantName")
                            .and("CompanyDetails.companyName").as("companyName")
            );
            return new ResponseWrapper<>(
                    true,
                    200,
                    "Returning all the application of the given user",
                    mongoTemplate.aggregate(aggregation, "applications", ApplicationDto.class).getMappedResults(),
                    null
            );
        } catch (Exception e) {
            return new ResponseWrapper<>(
                    false,
                    503,
                    "Operation is not successful due to some unknown server error.",
                    null,
                    e.getMessage()
            );
        }
    }

    public ResponseWrapper deleteAccount(String jwtToken) {
        String applicantId = JwtTokenUtil.getUserIdFromToken(jwtToken);
        if(!applicantDAO.existsById(applicantId)) {
            return new ResponseWrapper<>(
                false,
                404,
                "Applicant account not found.",
                null,
                null
            );
        }
        try {
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
        } catch (Exception e) {
            return new ResponseWrapper<>(
                false,
                503,
                "Account couldn't be deleted due to server error.",
                null,
                e.getMessage()
            );
        }
        return new ResponseWrapper<>(
            true,
            200,
            "Account has been deleted successfully.",
            null,
            null
        );
    }

    public ResponseWrapper removeApplication(String applicationId, String jwtToken) {
       String applicantId = JwtTokenUtil.getUserIdFromToken(jwtToken);
       try {
//           Optional<Application> optionalApplication = applicationDao.findById(applicationId);
           Optional<Application> optionalApplication = applicationDao.findByApplicationId(applicationId);
           if(optionalApplication.isEmpty() || !optionalApplication.get().getApplicantId().equals(applicantId)) {
               return new ResponseWrapper(
                       false,
                       404,
                       "Application not found.",
                       null,
                       null
               );
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
           return new ResponseWrapper(
                   true,
                   204,
                   "Application withdrawn successfully.",
                   null,
                   null
           );
       } catch (Exception e) {
           return new ResponseWrapper(
                   false,
                   500,
                   "Application couldn't be withdrawn because of some unknown server error.",
                   null,
                   e.getMessage()
           );
       }
    }
}
