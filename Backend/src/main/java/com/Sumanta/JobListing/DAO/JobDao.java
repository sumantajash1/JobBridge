package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.Entity.JobPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDao extends MongoRepository<JobPost, String> {
    public boolean existsByJobTitle(String jobTitle);

    public JobPost findByJobTitle(String jobTitle);
}
