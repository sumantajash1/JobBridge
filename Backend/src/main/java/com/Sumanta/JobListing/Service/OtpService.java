package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DTO.ResponseWrapper;

public interface OtpService {

     ResponseWrapper<String> generateOtpByGstNum(String gstNum);

     ResponseWrapper<String> verifyOtp(String mobNo, String otp);

     ResponseWrapper<String> generateOtpByMobNo(String mobNo);
}
