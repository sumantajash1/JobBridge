package com.Sumanta.JobListing.Exception;

public class DuplicateJobException extends RuntimeException {
    public DuplicateJobException(String jobTitle) {
        super("A job already exists with this job title : " + jobTitle);
    }
}
