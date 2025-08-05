package com.Sumanta.JobListing.Exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String credential) {
        super("Invalid " + credential + " provided by the user.");
    }
}
