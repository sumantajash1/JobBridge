package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.Entity.JobPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobDao extends MongoRepository<JobPost, String> {
    public boolean existsByJobTitle(String jobTitle);

    public JobPost findByJobTitle(String jobTitle);

    List<JobPost> findAllByCompanyIdAndActiveStatusTrue(String companyId);

    List<JobPost> findAllByCompanyIdAndActiveStatusFalse(String companyId);

    List<JobPost> findAllByActiveStatusTrueAndDeadlineBefore(LocalDate now);

    List<JobPost> findAllByCompanyId(String jobId);

    List<JobPost> findAllByActiveStatusTrue();

    List<JobPost> findByCompanyId(String gstNum);
}
