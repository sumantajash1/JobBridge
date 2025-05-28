package com.Sumanta.JobListing.Entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ApplicantData")
public class Applicant {
    private String aName;
    private String dob;
    private String password;
    private String mobNo;
    private String email;

    public Applicant() {
    }

    public Applicant(String aName, String dob, String password, String mobNo, String email) {
        this.aName = aName;
        this.dob = dob;
        this.password = password;
        this.mobNo = mobNo;
        this.email = email;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDob() {
        return this.dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Applicant{" +
                "aName='" + aName + '\'' +
                ", dob='" + dob + '\'' +
                ", password='" + password + '\'' +
                ", mobNo='" + mobNo + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
