package com.Sumanta.JobListing.Advice;

import com.Sumanta.JobListing.DTO.ApiResponse;
import com.Sumanta.JobListing.Exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericExceptions(Exception ex) {
        ApiResponse<String> response = new ApiResponse<>(false, 503, null, null, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApplicantNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleApplicantNotFoundException(ApplicantNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<String>(false, 404, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ApiResponse<String> response = new ApiResponse<String>(false, 401, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateApplicantException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateApplicantException(DuplicateApplicantException ex){
        ApiResponse<String> response = new ApiResponse<>(false, 409, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, 404, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleJobNotFoundException(JobNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, 404, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApplicantAlreadyAppliedToJobException.class)
    public ResponseEntity<ApiResponse<String>> handleApplicantAlreadyAppliedToJobException(ApplicantAlreadyAppliedToJobException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, 409, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleApplicationNotFoundException(ApplicationNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, 409, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateCompanyException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateCompanyException(DuplicateCompanyException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, 409, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateJobException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateJobException(DuplicateJobException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, 409, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
