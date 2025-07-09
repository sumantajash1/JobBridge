package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.ApplicantDAO;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Entity.Role;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicantService {
    @Autowired
    ApplicantDAO applicantDAO;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JobDao jobDao;

    public Pair<String, String> register(Applicant applicant) {
        String mobNo = applicant.getMobNo();
        if(alreadyExists(mobNo, applicant.getEmail()).equals("phone")) {
            return Pair.of("failed", alreadyExists(mobNo, applicant.getEmail()));
        }
        applicant.setPassword(passwordEncoder.encode(applicant.getPassword()));
        applicantDAO.save(applicant);
        String jwtToken = JwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return Pair.of(applicant.getAName(), jwtToken);
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
        return Pair.of(applicant.getAName(), jwtToken);
    }

    public List<JobPost> fetchAllJobs() {
       return jobDao.findAll();
    }
}
