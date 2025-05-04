package com.Sumanta.JobListing.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Applications")
public class Applications {
    private String sNO;
    private String name;
    // to Store resume
}
