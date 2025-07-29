package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.Entity.Application;
import com.Sumanta.JobListing.Entity.applicationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationDao extends MongoRepository<Application, String> {
    List<Application> findByApplicantId(String applicantId);

    List<Application> findAllByJobId(String jobId);

    List<Application> findAllByJobIdAndStatus(String jobId, applicationStatus applicationStatus);

    List<Application> deleteByApplicantId(String applicantId);

    Optional<Application> findByApplicationId(String applicationId);
}
