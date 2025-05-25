package com.Sumanta.JobListing.DTO;

public class CompanyLoginRequestBody {
    private String gstNum;
    private String password;

    public CompanyLoginRequestBody() {
    }

    public CompanyLoginRequestBody(String gstNum, String password) {
        this.gstNum = gstNum;
        this.password = password;
    }

    public String getGstNum() {
        return gstNum;
    }

    public void setGstNum(String gstNum) {
        this.gstNum = gstNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "CompanyLoginRequestBody{" +
                "gstNum='" + gstNum + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
