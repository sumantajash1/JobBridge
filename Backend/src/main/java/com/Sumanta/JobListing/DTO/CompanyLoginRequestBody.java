package com.Sumanta.JobListing.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class CompanyLoginRequestBody {
    private String gstNum;
    private String password;

}
