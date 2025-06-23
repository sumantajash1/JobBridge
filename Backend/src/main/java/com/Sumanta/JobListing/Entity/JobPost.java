package com.Sumanta.JobListing.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Document(collection = "JobPosts")
public class JobPost {
    @Id
    private String jobId;
    private String companyName;
    private String jobType; // Full Time, Internship
    private String jobTitle;
    private String salaryRange;
    private String yearsOfExperience;
    private String WorkType; // Onsite, Remote, Hybrid
    private String location;
    private String jobDescription;
    private List<String> requrements;
    private String benefitsAndPerks;
    private List<String> coreSkills;
    private boolean activeStatus = true;
    private String deadline; //can be null
    private int maxOpenings;

    public JobPost() {
    }

    public JobPost(String jobId, String companyName, String jobType, String jobTitle, String salaryRange, String yearsOfExperience, String workType, String location, String jobDescription, List<String> requrements, String benefitsAndPerks, List<String> coreSkills, boolean activeStatus, String deadline, int maxOpenings) {
        this.jobId = jobId;
        this.companyName = companyName;
        this.jobType = jobType;
        this.jobTitle = jobTitle;
        this.salaryRange = salaryRange;
        this.yearsOfExperience= yearsOfExperience;
        WorkType = workType;
        this.location = location;
        this.jobDescription = jobDescription;
        this.requrements = requrements;
        this.benefitsAndPerks = benefitsAndPerks;
        this.coreSkills = coreSkills;
        this.activeStatus = activeStatus;
        this.deadline = deadline;
        this.maxOpenings = maxOpenings;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(String yearsOfExperience) {
        this.yearsOfExperience= yearsOfExperience;
    }

    public String getWorkType() {
        return WorkType;
    }

    public void setWorkType(String workType) {
        WorkType = workType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public List<String> getRequrements() {
        return requrements;
    }

    public void setRequrements(List<String> requrements) {
        this.requrements = requrements;
    }

    public String getBenefitsAndPerks() {
        return benefitsAndPerks;
    }

    public void setBenefitsAndPerks(String benefitsAndPerks) {
        this.benefitsAndPerks = benefitsAndPerks;
    }

    public List<String> getCoreSkills() {
        return coreSkills;
    }

    public void setCoreSkills(List<String> coreSkills) {
        this.coreSkills = coreSkills;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getMaxOpenings() {
        return maxOpenings;
    }

    public void setMaxOpenings(int maxOpenings) {
        this.maxOpenings = maxOpenings;
    }

    @Override
    public String toString() {
        return "JobPost{" +
                "jobId='" + jobId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", jobType='" + jobType + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", salaryRange='" + salaryRange + '\'' +
                ", yearsOfExperience='" +  yearsOfExperience+ '\'' +
                ", WorkType='" + WorkType + '\'' +
                ", location='" + location + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", requrements=" + requrements +
                ", benefitsAndPerks='" + benefitsAndPerks + '\'' +
                ", coreSkills=" + coreSkills +
                ", activeStatus=" + activeStatus +
                ", deadline='" + deadline + '\'' +
                ", maxOpenings=" + maxOpenings +
                '}';
    }
}
