package com.Sumanta.JobListing.Entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CompanyData")
public class Company {
    private String gstNum;
    private String companyName;
    private String companyEmail;
    private String companyContactNum;
    private String companyPassword;
    private String estd;

    public Company() {
    }

    public Company(String gstNum, String companyName, String companyEmail, String companyPassword, String companyContactNum, String estd) {
        this.gstNum = gstNum;
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.companyPassword = companyPassword;
        this.companyContactNum = companyContactNum;
        this.estd = estd;
    }

    public String getGstNum() {
        return gstNum;
    }

    public void setGstNum(String gstNum) {
        this.gstNum = gstNum;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyContactNum() {
        return companyContactNum;
    }

    public void setCompanyContactNum(String companyContactNum) {
        this.companyContactNum = companyContactNum;
    }

    public String getCompanyPassword() {
        return companyPassword;
    }

    public void setCompanyPassword(String companyPassword) {
        this.companyPassword = companyPassword;
    }

    public String getEstd() {
        return estd;
    }

    public void setEstd(String estd) {
        this.estd = estd;
    }

    @Override
    public String toString() {
        return "Company{" +
                "gstNum='" + gstNum + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyEmail='" + companyEmail + '\'' +
                ", companyContactNum='" + companyContactNum + '\'' +
                ", companyPassword='" + companyPassword + '\'' +
                ", estd='" + estd + '\'' +
                '}';
    }
}
