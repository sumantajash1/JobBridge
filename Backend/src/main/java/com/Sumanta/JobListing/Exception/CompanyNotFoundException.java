package com.Sumanta.JobListing.Exception;

public class CompanyNotFoundException extends RuntimeException{
    public CompanyNotFoundException() {
        super("No Company/Employer is found with this credentials.");
    }
}
