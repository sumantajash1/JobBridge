package com.Sumanta.JobListing.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class AuthRequestBody {
    @NotBlank
    private String id;
    @NotBlank
    private String password;
}
