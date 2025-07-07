package com.Sumanta.JobListing.Entity;

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
    @Id
    private String gstNum;
    private String companyName;
    private String companyEmail;
    private String companyContactNum;
    private String companyPassword;
    private String estd;
}
