package com.Sumanta.JobListing.Service.impl;

import com.Sumanta.JobListing.Service.ResumeService;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class ResumeServiceImpl implements ResumeService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Override
    public String uploadResume(MultipartFile file) throws IOException {
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );
        return fileId.toHexString();
    }

    @Override
    public GridFSFile getFileById(String id) {
        return gridFsTemplate.findOne(
                org.springframework.data.mongodb.core.query.Query.query(
                        org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)
                )
        );
    }

    @Override
    public boolean deleteFileById(String id) {
        try {
            gridFsTemplate.delete(
              org.springframework.data.mongodb.core.query.Query.query(
                      org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)
              )
            );
        } catch (Exception e) {
           e.printStackTrace();
           return false;
        }
        return true;
    }
}
