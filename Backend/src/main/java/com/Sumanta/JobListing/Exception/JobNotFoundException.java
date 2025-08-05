package com.Sumanta.JobListing.Exception;

public class JobNotFoundException extends RuntimeException{
    public JobNotFoundException(String jobId) {
        super("No job is found with this id. JobId : " + jobId);
    }
}
