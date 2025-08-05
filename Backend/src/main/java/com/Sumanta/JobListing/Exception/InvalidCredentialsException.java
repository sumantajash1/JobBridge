package com.Sumanta.JobListing.Exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException() {
        super("Invalid credentials provided by the user.");
    }
}
