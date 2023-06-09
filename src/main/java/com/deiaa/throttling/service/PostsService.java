package com.deiaa.throttling.service;

import com.deiaa.throttling.Throttle.NoThrottlingStrategy;
import com.deiaa.throttling.Throttle.ThrottleStrategy;
import com.deiaa.throttling.repository.PostsRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class PostsService implements PostsRepository{

    @Autowired
    private HttpServletRequest request;

    private final ThrottleStrategy throttleStrategy;

    public PostsService() {
        this.throttleStrategy = new NoThrottlingStrategy();
    }

    @Override
    public String getPosts() throws InterruptedException {

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        String fullUrl = baseUrl + request.getRequestURI();

        this.throttleStrategy.throttleRequest(request.getRemoteAddr(), fullUrl);

        return fullUrl;
    }
}
