package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.ApplicantDAO;
import com.Sumanta.JobListing.DTO.ApplicantLoginRequestBody;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.Role;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import org.apache.commons.lang3.tuple.Pair;
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


    public Pair<String, String> register(Applicant applicant) {
        //System.out.println(applicant);
        String mobNo = applicant.getMobNo();
        if(alreadyExists(mobNo, applicant.getEmail()).equals("phone")) {
            return Pair.of("failed", "PhoneExists");
        }
        if(alreadyExists(mobNo, applicant.getEmail()).equals("email")) {
            return Pair.of("failed", "EmailExists");
        }
        applicant.setPassword(passwordEncoder.encode(applicant.getPassword()));
        applicantDAO.save(applicant);
        String jwtToken = jwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return Pair.of(applicant.getaName(), jwtToken);
    }

    private String alreadyExists(String mobNo, String email) {
        if(applicantDAO.existsByMobNo(mobNo)) {
            return "phone";
        }
        if(applicantDAO.existsByEmail(email)) {
            return "email";
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
        String jwtToken = jwtTokenUtil.GenerateToken(mobNo, Role.Applicant);
        return Pair.of(applicant.getaName(), jwtToken);
    }
}
