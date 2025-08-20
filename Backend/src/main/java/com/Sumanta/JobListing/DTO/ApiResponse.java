package com.Sumanta.JobListing.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private int httpStatusCode;
    private String message;
    private T data;
    private String error;


    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(
                true,
                200,
                message,
                data,
                null
        );
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(
                true,
                201,
                message,
                data,
                null
        );
    }

    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(
                true,
                204,
                "No content is found",
                null,
                null
        );
    }

}
