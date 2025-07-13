package com.Sumanta.JobListing.DTO;

import com.Sumanta.JobListing.Entity.applicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ApplicationDto {
    private String applicationId;
    private String jobId;
    private String applicantId;
    private String companyId;
    private String applicantName;
    private String companyName;
    private String resumeId;
    private applicationStatus status;
}
