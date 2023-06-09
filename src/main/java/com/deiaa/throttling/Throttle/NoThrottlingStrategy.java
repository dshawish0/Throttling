package com.deiaa.throttling.Throttle;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class NoThrottlingStrategy implements ThrottleStrategy{

    private final Map<String, Map<String, AtomicInteger>> rateLimiterMap;

    public NoThrottlingStrategy() {
        this.rateLimiterMap = new HashMap<>();

    }

    @Override
    public void throttleRequest(String ipAddress, String endPoint) {

        rateLimiterMap.computeIfAbsent(ipAddress, k -> new HashMap<>())
                .computeIfAbsent(endPoint, k -> new AtomicInteger(0))
                .incrementAndGet();


        System.out.println(rateLimiterMap);
    }

}
