package com.Sumanta.JobListing.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "JobPosts")
public class JobPost {
    @Id
    private String jobId;
    private String companyName;
    private String jobType; // Full Time, Internship
    private String jobTitle;
    private String salaryRange;
    private String yearsOfExperience;
    private String workType; // Onsite, Remote, Hybrid
    private String location;
    private String jobDescription;
    private List<String> requrements;
    private String benefitsAndPerks;
    private List<String> coreSkills;
    private String deadline; //can be null
    private int maxOpenings;

    private boolean activeStatus = true;
    private List<String> applicants;

}
