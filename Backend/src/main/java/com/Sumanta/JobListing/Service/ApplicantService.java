package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.ApplicantDAO;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.Role;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ApplicantService {
    @Autowired
    ApplicantDAO applicantDAO;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    PasswordEncoder passwordEncoder;


    public String register(Applicant applicant) {
        //System.out.println(applicant);
        String mobNo = applicant.getMobNo();
        if(alreadyExists(mobNo)) {
            return "AlreadyExists";
        }
        applicant.setPassword(passwordEncoder.encode(applicant.getPassword()));
        applicantDAO.save(applicant);
        String jwtToken = jwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return jwtToken;
    }

    public boolean alreadyExists(String mobNo) {
        if(applicantDAO.existsByMobNo(mobNo)) {
            return true;
        }
        return false;
    }
}
