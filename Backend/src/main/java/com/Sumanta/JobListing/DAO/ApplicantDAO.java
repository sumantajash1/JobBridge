package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.Entity.Applicant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantDAO extends MongoRepository<Applicant, String> {

    public Applicant findByMobNo(String mobNo);

    public boolean existsByMobNo(String mobNo);

    public boolean existsByEmail(String email);
}
