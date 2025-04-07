package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.PostDAO;
import com.Sumanta.JobListing.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.List;

@RestController
public class PostController {
    @Autowired
    PostDAO dao;
    @ApiIgnore
    @RequestMapping(value="/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("swagger-ui/index.html");
    }
    @GetMapping(value="posts")
    public List<Post> AllPosts() {
        return dao.findAll();
    }
    @PostMapping(value = "post")
    public void Store(@RequestBody Post post) {
        dao.save(post);
    }

}
