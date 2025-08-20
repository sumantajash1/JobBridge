package com.Sumanta.JobListing.Entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "ApplicantData")
public class Applicant {
    @NotBlank
    private String name;
    private LocalDate dob;
    @NotBlank
    private String password;
    @Id
    @NotBlank
    private String mobNo;
    @Email
    private String email;
}
