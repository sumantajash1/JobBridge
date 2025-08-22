package com.Sumanta.JobListing.Service;

import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.JobPost;

public interface LookUpService {

    Applicant getApplicantOrThrow(String mobileNum);

    JobPost getJobOrThrow(String jobId, String gstNum);

    Company getCompanyOrThrow(String mobileNum);
}
