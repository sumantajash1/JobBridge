package com.Sumanta.JobListing.DTO;

import com.Sumanta.JobListing.Entity.applicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class BasicDto {
    private String id;
    private String code;
    private Boolean status;
    private applicationStatus applicationStatus;
}
