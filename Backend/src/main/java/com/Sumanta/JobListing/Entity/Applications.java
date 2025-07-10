package com.Sumanta.JobListing.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document("applications")
public class Applications {
    @Id
    private String applicationId;
    private String jobId; //To be taken from client
    private String applicantId; // To be taken from client
    private String companyId;// To be taken from client
    private String applicantName;
    private String companyName;
    private String resumeId;
    private applicationStatus status;
}
