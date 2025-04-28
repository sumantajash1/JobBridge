package com.Sumanta.JobListing.DAO;

import com.Sumanta.JobListing.model.Post;

import java.util.List;

public interface Search
{
    public List<Post> SearchByText(String text);
}
