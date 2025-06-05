package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.Entity.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyDAO extends MongoRepository<Company, String>{
    public boolean existsByGstNum(String gstNum);

    public Company findByGstNum(String gstNum);

    public boolean existsByCompanyName(String companyName);

    public boolean existsByCompanyContactNum(String companyContactNum);

    public boolean existsByCompanyEmail(String companyEmail);
}

