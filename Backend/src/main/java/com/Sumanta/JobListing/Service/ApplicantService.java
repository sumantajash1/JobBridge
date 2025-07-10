package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.ApplicantDAO;
import com.Sumanta.JobListing.DAO.ApplicationDao;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.Entity.*;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import com.twilio.twiml.voice.Application;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
        if(applicantDAO.existsByMobNo(mobileNo)) {
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
        if (!doesExists(mobNo)) {
            return "ApplicantNotFound";
        }
        Applicant applicant = applicantDAO.findByMobNo(mobNo);
        applicant.setPassword(passwordEncoder.encode(newPassword));
        applicantDAO.save(applicant);
        return "PasswordResetSuccess";
    }

    public String applyToJob(String jobId, String applicantId, String applicantName, String companyId, String companyName, String resumeId) {
       List<Applications> applicationsList = applicationDao.findByApplicantId(applicantId);
       Boolean alreadyApplied = applicationsList.stream().anyMatch(app -> app.getJobId().equals(jobId));
       if(alreadyApplied) {
           return "alreadyApplied";
       }
       Applications application = new Applications();
       application.setApplicantId(applicantId);
       application.setApplicantName(applicantName);
       application.setJobId(jobId);
       application.setCompanyId(companyId);
       application.setCompanyName(companyName);
       application.setResumeId(resumeId);
       application.setStatus(applicationStatus.PENDING);
       applicationDao.save(application);
       Optional<JobPost> temp = jobDao.findById(jobId);
       if(temp.isEmpty()) {
           return "JobDontExist";
       }
       JobPost jobPost = temp.get();
       List<String> applicants = jobPost.getApplicants();
       applicants.add(applicantId);
       jobDao.save(jobPost);
       return "SUCCESS";
    }
}
