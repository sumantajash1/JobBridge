package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DTO.ResponseWrapper;
import com.Sumanta.JobListing.Entity.Company;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OtpService {
    @Autowired
    CompanyDAO companyDAO;

    @Value("${ACCOUNT_SID}")
    private String ACCOUNT_SID;
    @Value("${AUTH_TOKEN}")
    private String AUTH_TOKEN;
    @Value("${SERVICE_SID}")
    private String SERVICE_SID;

    public ResponseWrapper<String> generateOtpByGstNum(String gstNum) {
        Company company = companyDAO.findByGstNum(gstNum);
        if(company==null) {
             return new ResponseWrapper<>(
                    false,
                    404,
                    "Company Not found.",
                    null,
                    null
            );
        }
        String mobNo = "+91" + company.getCompanyContactNum();
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try {
            Verification verification = Verification.creator(
                    SERVICE_SID,
                    mobNo,
                    "sms"
            ).create();
        } catch (Exception e) {
            return new ResponseWrapper<>(
                    false,
                    503,
                    "OTP couldn't be generated.",
                    null,
                    e.getMessage()
            );
        }
        return new ResponseWrapper<>(
                true,
                200,
                "OTP sent successfully.",
                mobNo,
                null
        );
    }

    public ResponseWrapper<String> verifyOtp(String mobNo, String otp) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try{
            log.info("SUMANTA : Mobile No. for otp Verification : " +  mobNo + ", OTP : " + otp);
            VerificationCheck verificationCheck = VerificationCheck
                    .creator(SERVICE_SID)
                    .setTo(mobNo)
                    .setCode(otp)
                    .create();
            log.info("SUMANTA : Verification status : " + verificationCheck.getStatus());
        } catch (Exception e) {
            return new ResponseWrapper<>(
                    false,
                    503,
                    "OTP couldn't be verified.",
                    null,
                    e.getMessage()
            );
        }
        return new ResponseWrapper(
                true,
                200,
                "OTP is Verified",
                mobNo,
                null
        );
    }

    public ResponseWrapper<String> generateOtpbyMobNo(String mobNo) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try {
            Verification verification = Verification.creator(
                    SERVICE_SID,
                    "+91"+mobNo,
                    "sms"
            ).create();
        } catch (Exception e) {
            return new ResponseWrapper(
                    false,
                    503,
                    "OTP couldn't be generated.",
                    null,
                    e.getMessage()
            );
        }
        return new ResponseWrapper(
                true,
                200,
                "OTP sent to the user's mobile number.",
                mobNo,
                null
        );
    }

}
