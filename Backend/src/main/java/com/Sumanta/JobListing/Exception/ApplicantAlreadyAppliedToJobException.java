package com.Sumanta.JobListing.Exception;

public class ApplicantAlreadyAppliedToJobException extends RuntimeException{
    public ApplicantAlreadyAppliedToJobException(String jobId) {
        super("Applicant has already applied to this job. JobId : " + jobId);
    }
}
