package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.Entity.Applications;
import com.twilio.twiml.voice.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationDao extends MongoRepository<Applications, String> {
    List<Applications> findByApplicantId(String applicantId);
}
