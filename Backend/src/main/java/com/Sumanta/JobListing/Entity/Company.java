package com.Sumanta.JobListing.Entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CompanyData")
public class Company {
    private String gstNum;
    private String cName;
    private String cEmail;
    private String cContactNumber;
    private String cPassword;
    private String estd;

    public Company() {
    }

    public Company(String gstNum, String cName, String cPassword, String estd, String cEmail, String cContactNumber) {
        this.cEmail = cEmail;
        this.cContactNumber = cContactNumber;
        this.gstNum = gstNum;
        this.cName = cName;
        this.cPassword = cPassword;
        this.estd = estd;
    }

    public String getcEmail() {
        return cEmail;
    }

    public void setcEmail(String cEmail) {
        this.cEmail = cEmail;
    }

    public String getcContactNumber() {
        return cContactNumber;
    }

    public void setcContactNumber(String cContactNumber) {
        this.cContactNumber = cContactNumber;
    }

    public String getGstNum() {
        return gstNum;
    }

    public void setGstNum(String gstNum) {
        this.gstNum = gstNum;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcPassword() {
        return cPassword;
    }

    public void setcPassword(String cPassword) {
        this.cPassword = cPassword;
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
                ", cName='" + cName + '\'' +
                ", cEmail='" + cEmail + '\'' +
                ", cContactNumber='" + cContactNumber + '\'' +
                ", cPassword='" + cPassword + '\'' +
                ", estd='" + estd + '\'' +
                '}';
    }
}
