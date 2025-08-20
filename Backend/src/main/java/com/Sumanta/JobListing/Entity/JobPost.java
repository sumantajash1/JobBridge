package com.Sumanta.JobListing.Entity;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "JobPosts")
public class JobPost {
    @NotBlank
    @Id
    private String jobId;
    @NotBlank
    private String companyName;
    @NotBlank
    private String jobType; // Full Time, Internship
    @NotBlank
    private String jobTitle;
    @NotBlank
    private String salaryRange;
    @NotBlank
    private String yearsOfExperience;
    @NotBlank
    private String workType; // Onsite, Remote, Hybrid
    @NotBlank
    private String location;
    @NotBlank
    private String jobDescription;
    @NotBlank
    private List<String> requirements;
    @NotBlank
    private String benefitsAndPerks;
    @NotBlank
    private List<String> coreSkills;
    private LocalDate deadline; //can be null
    @NotBlank
    private int maxOpenings;

    private boolean activeStatus = true;
    private List<String> applicants;
    private String companyId;
}
