package com.deiaa.throttling.service;

import com.deiaa.throttling.Throttle.DynamicRequestThrottlingStrategy;
import com.deiaa.throttling.Throttle.ThrottleStrategy;
import com.deiaa.throttling.repository.HistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class HistoryService implements HistoryRepository {

    @Autowired
    private HttpServletRequest request;

    private final ThrottleStrategy throttleStrategy;

    public HistoryService() {
        this.throttleStrategy = new DynamicRequestThrottlingStrategy();
    }


    @Override
    public String getHistory() throws InterruptedException{

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        String fullUrl = baseUrl + request.getRequestURI();

        this.throttleStrategy.throttleRequest(request.getRemoteAddr(), fullUrl);

        return fullUrl;
    }
}
