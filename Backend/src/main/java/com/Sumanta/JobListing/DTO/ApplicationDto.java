package com.Sumanta.JobListing.DTO;

import com.Sumanta.JobListing.Entity.enums.applicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ApplicationDto {
    @NotBlank
    private String applicationId;
    @NotBlank
    private String jobId;
    @NotBlank
    private String applicantId;
    @NotBlank
    private String companyId;
    @NotBlank
    private String applicantName;
    @NotBlank
    private String companyName;
    @NotBlank
    private String resumeId;
    @NotBlank
    private applicationStatus status;
}
