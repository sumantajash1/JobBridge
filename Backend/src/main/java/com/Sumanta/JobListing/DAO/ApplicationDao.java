package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.Entity.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationDao extends MongoRepository<Application, String> {
    List<Application> findByApplicantId(String applicantId);
}
