package com.Sumanta.JobListing.Service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResumeService {

    String uploadResume(MultipartFile file) throws IOException;

    GridFSFile getFileById(String id);

    boolean deleteFileById(String id);

}
