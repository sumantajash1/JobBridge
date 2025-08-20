package com.Sumanta.JobListing.Service.impl;

import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DTO.ApiResponse;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Exception.CompanyNotFoundException;
import com.Sumanta.JobListing.Service.OtpService;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OtpServiceImpl implements OtpService{
    @Autowired
    CompanyDAO companyDAO;

    @Value("${ACCOUNT_SID}")
    private String ACCOUNT_SID;
    @Value("${AUTH_TOKEN}")
    private String AUTH_TOKEN;
    @Value("${SERVICE_SID}")
    private String SERVICE_SID;

    @Override
    public ApiResponse<String> generateOtpByGstNum(String gstNum) {
        Company company = companyDAO.findByGstNum(gstNum);
        if(company==null) {
            throw new CompanyNotFoundException(gstNum);
        }
        String mobNo = "+91" + company.getCompanyContactNum();
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Verification verification = Verification.creator(
                SERVICE_SID,
                mobNo,
                "sms"
        ).create();
        return ApiResponse.ok(mobNo, "OTP sent successfully");
    }

    @Override
    public ApiResponse<String> verifyOtp(String mobNo, String otp) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        VerificationCheck verificationCheck = VerificationCheck
                .creator(SERVICE_SID)
                .setTo(mobNo)
                .setCode(otp)
                .create();
        return ApiResponse.ok(mobNo, "OTP is verified");
    }

    @Override
    public ApiResponse<String> generateOtpByMobNo(String mobNo) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Verification verification = Verification.creator(
                    SERVICE_SID,
                    "+91"+mobNo,
                    "sms"
            ).create();
        return ApiResponse.ok(mobNo, "OTP sent to the user's mobile number");
    }
}
