package com.Sumanta.JobListing.DTO;

import com.Sumanta.JobListing.Entity.applicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class BasicDto {
    @NotBlank
    private String id;
    @NotBlank
    private String code;
    @NotBlank
    private Boolean status;
    @NotBlank
    private applicationStatus applicationStatus;
}
