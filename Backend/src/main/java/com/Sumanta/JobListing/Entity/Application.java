package com.Sumanta.JobListing.Entity;

import com.Sumanta.JobListing.Entity.enums.applicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document("applications")
public class Application {
    @Id
    private String applicationId;
    @NotBlank
    private String jobId; //To be taken from client
    @NotBlank
    private String applicantId; // To be taken from client
    @NotBlank
    private String companyId;// To be taken from client
    @NotBlank
    private String resumeId;
    private applicationStatus status;
}
