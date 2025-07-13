package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.ApplicantDAO;
import com.Sumanta.JobListing.DAO.ApplicationDao;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.DTO.ApplicationDto;
import com.Sumanta.JobListing.Entity.*;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public Pair<String, String> register(Applicant applicant) {
        String mobNo = applicant.getMobNo();
        if(alreadyExists(mobNo, applicant.getEmail()).equals("PhoneExists")) {
            return Pair.of("failed", "Phone number already exists");
        }
        if(alreadyExists(mobNo, applicant.getEmail()).equals("EmailExists")) {
            return Pair.of("failed", "Email address already exists");
        }
        applicant.setPassword(passwordEncoder.encode(applicant.getPassword()));
        applicantDAO.save(applicant);
        String jwtToken = JwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return Pair.of(applicant.getName(), jwtToken);
    }

    private String alreadyExists(String mobNo, String email) {
        if(applicantDAO.existsByMobNo(mobNo)) {
            return "PhoneExists";
        }
        if(applicantDAO.existsByEmail(email)) {
            return "EmailExists";
        }
        return "No";
    }

    private boolean doesExists(String mobileNo) {
        if(applicantDAO.existsById(mobileNo)) {
            return true;
        }
        return false;
    }

    public Pair<String, String> Login(ApplicantLoginRequestBody applicantLoginRequestBody) {
        String mobNo = applicantLoginRequestBody.getMobileNo();
        if(!doesExists(mobNo)) {
            return Pair.of("failed", "Doesn't Exist");
        }
        Applicant applicant = applicantDAO.findByMobNo(mobNo);
        if(!passwordEncoder.matches(applicantLoginRequestBody.getPassword(), applicant.getPassword())) {
            return Pair.of("failed", "Wrong Password");
        }
        String jwtToken = JwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return Pair.of(applicant.getName(), jwtToken);
    }

    public List<JobPost> fetchAllJobs() {
       return jobDao.findAll();
    }

    public String resetPassword(String mobNo, String newPassword) {
        if (doesExists(mobNo)) {
            return "ApplicantNotFound";
        }
        Applicant applicant = applicantDAO.findByMobNo(mobNo);
        applicant.setPassword(passwordEncoder.encode(newPassword));
        applicantDAO.save(applicant);
        return "PasswordResetSuccess";
    }

    public String applyToJob(String jobId, String applicantId, String companyId, String resumeId) {
        if(!doesExists(applicantId)) {
            return "ApplicantNotFound";
        } else if(!jobDao.existsById(jobId)) {
            return "JobNotFound";
        }
       List<Application> applicationsList = applicationDao.findByApplicantId(applicantId);
       boolean alreadyApplied = applicationsList.stream().anyMatch(app -> app.getJobId().equals(jobId));
       if(alreadyApplied) {
           return "alreadyApplied";
       }
       Application application = new Application();
       application.setApplicantId(applicantId);
       application.setJobId(jobId);
       application.setCompanyId(companyId);
       application.setResumeId(resumeId);
       application.setStatus(applicationStatus.PENDING);
       applicationDao.save(application);
       Optional<JobPost> temp = jobDao.findById(jobId);
       if(temp.isEmpty() || !temp.get().isActiveStatus()) {
           return "JobDontExist";
       }
       JobPost jobPost = temp.get();
       List<String> applicants = jobPost.getApplicants();
       applicants.add(applicantId);
       jobDao.save(jobPost);
       return "SUCCESS";
    }

    public List<ApplicationDto> getAllApplications(String applicantId) {
        if(!doesExists(applicantId)) {
            return null;
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("ApplicantData", "mobNO", "mobNo", "applicantDetails"),
                Aggregation.lookup("CompanyData", "gstNum", "gstNum", "CompanyDetails"),
                Aggregation.unwind("applicantDetails", true),
                Aggregation.unwind("CompanyDetails", true),
                Aggregation.project("applicationId", "jobId", "applicantId", "companyId", "resumeId", "status")
                        .and("_id").as("applicationId")
                        .and("applicantDetails.name").as("applicantName")
                        .and("CompanyDetails.companyName").as("companyName")
        );

        return mongoTemplate.aggregate(aggregation, "applications", ApplicationDto.class).getMappedResults();
    }
}
