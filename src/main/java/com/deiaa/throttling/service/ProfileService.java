package com.deiaa.throttling.service;

import com.deiaa.throttling.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ProfileService implements ProfileRepository {

    @Autowired
    private HttpServletRequest request;

    @Override
    public String getProfile() {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        return baseUrl + request.getRequestURI();
    }
}
