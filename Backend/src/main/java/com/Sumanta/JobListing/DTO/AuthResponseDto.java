package com.Sumanta.JobListing.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    @NotBlank
    private String username;
    @NotBlank
    private String jwtToken;
}
