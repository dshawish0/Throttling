package com.deiaa.throttling.controller;

import com.deiaa.throttling.service.PostsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    @Autowired
    private PostsService postsService;

    @GetMapping
    public ResponseEntity<String> getPosts() throws InterruptedException {
        return ResponseEntity.ok(postsService.getPosts());
    }

    public void setPostService(PostsService mockPostsService) {
        this.postsService = mockPostsService;
    }
}
