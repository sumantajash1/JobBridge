package com.Sumanta.JobListing.Service.impl;

import com.Sumanta.JobListing.DAO.ApplicantDAO;
import com.Sumanta.JobListing.DAO.CompanyDAO;
import com.Sumanta.JobListing.DAO.JobDao;
import com.Sumanta.JobListing.Entity.Applicant;
import com.Sumanta.JobListing.Entity.Company;
import com.Sumanta.JobListing.Entity.JobPost;
import com.Sumanta.JobListing.Exception.ApplicantNotFoundException;
import com.Sumanta.JobListing.Exception.CompanyNotFoundException;
import com.Sumanta.JobListing.Exception.JobNotFoundException;
import com.Sumanta.JobListing.Service.LookUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LookUpServiceImp implements LookUpService {

    @Autowired
    JobDao jobDao;
    @Autowired
    ApplicantDAO applicantDAO;
    @Autowired
    CompanyDAO companyDAO;

    @Override
    public JobPost getJobOrThrow(String jobId, String gstNum) {
        return jobDao.findById(jobId).filter(job -> job.getCompanyId().equals(gstNum)).orElseThrow(() -> new JobNotFoundException(jobId));
    }

    @Override
    public Applicant getApplicantOrThrow(String mobileNum) {
        return applicantDAO.findById(mobileNum).orElseThrow(() -> new ApplicantNotFoundException());
    }

    @Override
    public Company getCompanyOrThrow(String gstNum) {
        return companyDAO.findById(gstNum).orElseThrow(() -> new CompanyNotFoundException(gstNum));
    }

}
