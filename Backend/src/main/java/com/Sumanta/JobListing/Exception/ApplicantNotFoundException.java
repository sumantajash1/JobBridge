package com.Sumanta.JobListing.Exception;

public class ApplicantNotFoundException extends RuntimeException{

   public ApplicantNotFoundException() {
        super("Applicant is not registered with us.");
   }

}
