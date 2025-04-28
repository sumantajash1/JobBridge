package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.model.Post;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import com.mongodb.client.AggregateIterable;

@Component
public class SearchClass implements Search {
    @Autowired
    MongoClient client;
    @Autowired
    MongoConverter converter;

    public List<Post> SearchByText(String text) {
        List<Post> SearchResult = new ArrayList<>();
        MongoDatabase database = client.getDatabase("Sumanta");
        MongoCollection<Document> collection = database.getCollection("JobPost");
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search", new Document("index", "default").append("text", new Document("query", text).append("path", Arrays.asList("techs", "desc", "profile")))), new Document("$sort", new Document("exp", 1L))));
        result.forEach(document -> SearchResult.add(converter.read(Post.class, document)));
        return SearchResult;
    }
}
