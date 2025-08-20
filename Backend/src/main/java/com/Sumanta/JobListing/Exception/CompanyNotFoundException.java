package com.Sumanta.JobListing.Exception;

public class CompanyNotFoundException extends RuntimeException{
    public CompanyNotFoundException(String gstNum) {
        super("No Company/Employer is found with this credentials. Gst Number : " + gstNum);
    }
}
