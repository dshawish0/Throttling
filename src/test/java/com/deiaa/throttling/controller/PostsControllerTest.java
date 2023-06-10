package com.deiaa.throttling.controller;

import com.deiaa.throttling.Throttle.ThrottleStrategy;
import com.deiaa.throttling.service.PostsService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PostsControllerTest {

    @Mock
    private PostsService postsService;

    @InjectMocks
    private PostsController postsController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ThrottleStrategy throttleStrategy;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPost_Success() throws InterruptedException {

        String expectedResponse = "http://localhost:8080/api/posts";
        when(postsService.getPosts()).thenReturn(expectedResponse);

        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/posts");

        doNothing().when(throttleStrategy).throttleRequest(anyString(), anyString());

        PostsService mockPostsService = new PostsService();
        mockPostsService.setThrottleStrategy();
        mockPostsService.setRequest(request);
        postsController.setPostService(mockPostsService);

        ResponseEntity<String> response = postsController.getPosts();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testGetPost_NoThrottlingException() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(60);
        List<Future<?>> futures = new ArrayList<>();


        String expectedResponse = "http://localhost:8080/api/posts";
        when(postsService.getPosts()).thenReturn(expectedResponse);

        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/posts");

        doNothing().when(throttleStrategy).throttleRequest(anyString(), anyString());

        PostsService mockPostsService = new PostsService();
        mockPostsService.setThrottleStrategy();
        mockPostsService.setRequest(request);
        postsController.setPostService(mockPostsService);

        for (int i = 1; i <= 100; i++) {
            futures.add(executorService.submit(() -> {
                ResponseEntity<String> response = null;
                try {
                    response = postsController.getPosts();
                    assertEquals(HttpStatus.OK, response.getStatusCode());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
    }


}
