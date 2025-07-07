package com.Sumanta.JobListing.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "ApplicantData")
public class Applicant {
    private String aName;
    private String dob;
    private String password;
    @Id
    private String mobNo;
    private String email;
}
