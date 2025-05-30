package com.Sumanta.JobListing.DTO;

public class ApplicantLoginRequestBody {
    String mobileNo;
    String password;

    public ApplicantLoginRequestBody() {};

    public ApplicantLoginRequestBody(String mobileNo, String password) {
        this.mobileNo = mobileNo;
        this.password = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ApplicantLoginRequestBody{" +
                "mobileNo='" + mobileNo + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
