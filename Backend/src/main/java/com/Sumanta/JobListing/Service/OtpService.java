package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.Entity.Company;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public String generateOtp(String gstNum) {
        Company company = companyDAO.findByGstNum(gstNum);
        if(company==null) {
            return "UserNotExist";
        }
        String mobNo = "+91" + company.getCompanyContactNum();
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try {
            Verification verification = Verification.creator(
                    SERVICE_SID,
                    mobNo,
                    "sms"
            ).create();
            System.out.println(verification.getStatus());
        } catch (Exception e) {
           e.printStackTrace();
            System.out.println("Otp couldn't be generated");
            return "OtpNotGenerated";
        }
        System.out.println("Otp Generated Successfully");
        return mobNo;
    }

    public String verifyOtp(String mobileNum, String otp) {
        System.out.println("verifyOTPService - " + mobileNum + " OTP -> " + otp);

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try{
            VerificationCheck verificationCheck = VerificationCheck
                    .creator(SERVICE_SID)
                    .setTo(mobileNum)
                    .setCode(otp)
                    .create();
            System.out.println(verificationCheck.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return "WRONG";
        }
        return "RIGHT";
    }

    public String generateOtpbyMobNo(String mobNo) {
        mobNo = "+91" + mobNo;
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try {
            Verification verification = Verification.creator(
                    SERVICE_SID,
                    mobNo,
                    "sms"
            ).create();
            System.out.println(verification.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Otp couldn't be generated");
            return "OtpNotGenerated";
        }
        System.out.println("Otp Generated Successfully");
        return mobNo;
    }

}
