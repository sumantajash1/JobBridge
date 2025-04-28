package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostDAO extends MongoRepository<Post,String> {

}
