package com.Sumanta.JobListing.Entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Applicants")
public class Applicant {
    // aName, dob, password, mobile no., email
    private String aId; //generate inside service
    private String aName;
    private String aDob;
    private String password;
    private String mobNo;
    private String Email;

    public Applicant() {
    }

    public Applicant(String id, String aName, String aDob, String password, String mobNo, String email) {
        this.aId = id;
        this.aName = aName;
        this.aDob = aDob;
        this.password = password;
        this.mobNo = mobNo;
        Email = email;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
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

    public String getAdob() {
        return aDob;
    }

    public void setAdob(String aDob) {
        this.aDob = aDob;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public String toString() {
        return "Applicant{" +
                "aId='" + aId + '\'' +
                ", aName='" + aName + '\'' +
                ", aDob='" + aDob + '\'' +
                ", password='" + password + '\'' +
                ", mobNo='" + mobNo + '\'' +
                ", Email='" + Email + '\'' +
                '}';
    }
}
