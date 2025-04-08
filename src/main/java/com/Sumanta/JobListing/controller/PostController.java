package com.Sumanta.JobListing.controller;

import com.Sumanta.JobListing.DAO.PostDAO;
import com.Sumanta.JobListing.DAO.Search;
import com.Sumanta.JobListing.DAO.SearchClass;
import com.Sumanta.JobListing.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@RestController
public class PostController {
    @Autowired
    PostDAO dao;
    @Autowired
    SearchClass search;

    @ApiIgnore
    @RequestMapping(value="/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("swagger-ui/index.html");
    }

    @GetMapping(value="posts")
    public List<Post> AllPosts() {
        return dao.findAll();
    }

    @GetMapping("/posts/{text}")
    public List<Post> search(@PathVariable("text") String text) {
        return search.SearchByText(text);
    }

    @PostMapping(value = "post")
    public void Store(@RequestBody Post post) {
        dao.save(post);
    }

}
