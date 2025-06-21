package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DTO.CompanyLoginRequestBody;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.Role;
import com.Sumanta.JobListing.utils.GstNumberValidator;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    PasswordEncoder passwordEncoder;

    public Pair<String, String> register(Company company) {
        System.out.println(GstNumberValidator.isGstNumValid(company.getGstNum()));
        if(!GstNumberValidator.isGstNumValid(company.getGstNum())) {
            return Pair.of("failed", "InvalidGST");
        }
        String existsResponse = alreadyExists(company);
        System.out.println(existsResponse);
        if(!existsResponse.equals("NO")) {
            return Pair.of("failed", existsResponse);
        }
        company.setCompanyPassword(passwordEncoder.encode(company.getCompanyPassword()));
        companyDAO.save(company);
        return Pair.of(company.getCompanyName(), JwtTokenUtil.GenerateToken(company.getGstNum(), Role.Company));
    }

    public Pair<String, String> Login(CompanyLoginRequestBody companyLoginRequestBody) {
        System.out.println(GstNumberValidator.isGstNumValid(companyLoginRequestBody.getGstNum()));
        String gstNum = companyLoginRequestBody.getGstNum();
        if(!GstNumberValidator.isGstNumValid(gstNum)) {
            return Pair.of("failed", "InvalGstNum");
        }
        if(!companyDAO.existsByGstNum(gstNum)) {
            return Pair.of("failed", "NotFound");
        }
        Company tempCompany = companyDAO.findByGstNum(gstNum);
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
            System.out.println(company.getCompanyName());
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

}
