package com.Sumanta.JobListing.Exception;

public class DuplicateApplicantException extends RuntimeException{
    public DuplicateApplicantException(String conflictedCredential) {
        super("An applicant with this " + conflictedCredential + " already exists");
    }
}
