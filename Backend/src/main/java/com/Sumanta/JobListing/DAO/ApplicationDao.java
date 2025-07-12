package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.Entity.Application;
import com.Sumanta.JobListing.Entity.applicationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationDao extends MongoRepository<Application, String> {
    List<Application> findByApplicantId(String applicantId);

    List<Application> findAllByJobId(String jobId);

    List<Application> findAllByJobIdAndStatus(String jobId, applicationStatus applicationStatus);
}
