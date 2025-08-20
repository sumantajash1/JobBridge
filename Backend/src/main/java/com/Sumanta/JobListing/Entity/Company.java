package com.Sumanta.JobListing.Entity;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "CompanyData")
public class Company {
    @NotBlank
    @Id
    private String gstNum;
    @NotBlank
    private String companyName;
    @NotBlank
    private String companyEmail;
    @NotBlank
    private String companyContactNum;
    @NotBlank
    private String companyPassword;
    private String estd;
}
