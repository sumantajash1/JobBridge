package com.Sumanta.JobListing.Advice;

import com.Sumanta.JobListing.DTO.ResponseWrapper;
import com.Sumanta.JobListing.Exception.*;
import com.twilio.http.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<String>> handleGenericExceptions(Exception ex) {
        ResponseWrapper<String> response = new ResponseWrapper<>(false, 503, "An unexpected server error occured, please try later.", null, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApplicantNotFoundException.class)
    public ResponseEntity<ResponseWrapper<String>> handleApplicantNotFoundException(ApplicantNotFoundException ex) {
        ResponseWrapper<String> response = new ResponseWrapper<String>(false, 404, "Applicant is not registered with us.", null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseWrapper<String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ResponseWrapper<String> response = new ResponseWrapper<String>(false, 401, "Invalid Credentials has been entered by the user.", null, null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateApplicantException.class)
    public ResponseEntity<ResponseWrapper<String>> handleDuplicateApplicantException(){
        ResponseWrapper<String> response = new ResponseWrapper<>(false, 409, "An applicant already exists with entered credentials.", null, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ResponseWrapper<String>> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        ResponseWrapper<String> response = new ResponseWrapper<>(false, 404, "Company not found.", null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ResponseWrapper<String>> handleJobNotFoundException(JobNotFoundException ex) {
        ResponseWrapper<String> response = new ResponseWrapper<>(false, 404, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApplicantAlreadyAppliedToJobException.class)
    public ResponseEntity<ResponseWrapper<String>> handleApplicantAlreadyAppliedToJobException(ApplicantAlreadyAppliedToJobException ex) {
        ResponseWrapper<String> response = new ResponseWrapper<>(false, 409, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ApplicantAlreadyAppliedToJobException.class)
    public ResponseEntity<ResponseWrapper<String>> handleApplicationNotFoundException(ApplicationNotFoundException ex) {
        ResponseWrapper<String> response = new ResponseWrapper<>(false, 409, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
