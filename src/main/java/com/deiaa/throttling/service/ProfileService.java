package com.deiaa.throttling.service;

import com.deiaa.throttling.Throttle.LinearDelayThrottlingStrategy;
import com.deiaa.throttling.Throttle.ThrottleStrategy;
import com.deiaa.throttling.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ProfileService implements ProfileRepository {

    @Autowired
    private HttpServletRequest request;

    private final ThrottleStrategy throttleStrategy;

    public ProfileService() {
        this.throttleStrategy = new LinearDelayThrottlingStrategy();
    }


    @Override
    public String getProfile() throws InterruptedException {

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        String fullUrl = baseUrl + request.getRequestURI();
        this.throttleStrategy.throttleRequest(request.getRemoteAddr(), fullUrl);

        return fullUrl;
    }
}
