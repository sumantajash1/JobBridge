package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.DTO.ApiResponse;

public interface OtpService {

     ApiResponse<String> generateOtpByGstNum(String gstNum);

     ApiResponse<String> verifyOtp(String mobNo, String otp);

     ApiResponse<String> generateOtpByMobNo(String mobNo);
}
