package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.Role;
import com.Sumanta.JobListing.utils.GstNumberValidator;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
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
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    GstNumberValidator gstNumberValidator;

    public String register(Company company) {
        if(!gstNumberValidator.isGstNumValid(company.getGstNum())) {
            return "InvalGstNum";
        }
        if(doesExist(company.getGstNum())) {
            return "Exists";
        }
        company.setcPassword(passwordEncoder.encode(company.getcPassword()));
        companyDAO.save(company);
        return jwtTokenUtil.GenerateToken(company.getGstNum(), Role.Company);
    }

    public boolean doesExist(String gstNum) {
        return companyDAO.existsByGstNum(gstNum);
    }

    public List<Company> fetchAll() {
        return companyDAO.findAll();
    }

    public String Login(Company company) {
        String gstNum = company.getGstNum();
        if(!gstNumberValidator.isGstNumValid(gstNum)) {
            return "InvalGstNum";
        }
        if(!doesExist(gstNum)) {
            return "NotFound";
        }
        Company tempCompany = companyDAO.findByGstNum(gstNum);
        if(!passwordEncoder.matches(company.getcPassword(), tempCompany.getcPassword())) {
           return "WrongPassword";
        }
        return jwtTokenUtil.GenerateToken(gstNum, Role.Company);
    }
}
