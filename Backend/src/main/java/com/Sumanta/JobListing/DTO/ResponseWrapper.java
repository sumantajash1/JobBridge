package com.Sumanta.JobListing.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper<T> {
    private boolean success;
    private int httpStatusCode;
    private String message;
    private T data;
    private String error;
}
