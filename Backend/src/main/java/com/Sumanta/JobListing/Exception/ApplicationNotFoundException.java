package com.Sumanta.JobListing.Exception;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(String applicationId) {
        super("No application is found with this application id. applicationId : " + applicationId);
    }
}
