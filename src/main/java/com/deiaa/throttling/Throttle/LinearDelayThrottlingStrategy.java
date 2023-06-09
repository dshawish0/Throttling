package com.deiaa.throttling.Throttle;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LinearDelayThrottlingStrategy implements ThrottleStrategy{

    private final Map<String, Map<String, AtomicInteger>> rateLimiterMap;
    private final Map<String, Map<String, Long>> timestampMap;

    public LinearDelayThrottlingStrategy() {
        this.rateLimiterMap = new HashMap<>();
        timestampMap = new HashMap<>();

    }

    @Override
    public void throttleRequest(String ipAddress, String endPoint) {

        rateLimiterMap.computeIfAbsent(ipAddress, k -> new HashMap<>())
                .computeIfAbsent(endPoint, k -> new AtomicInteger(0))
                .incrementAndGet();

        int requestCount = rateLimiterMap.get(ipAddress).get(endPoint).get();

        if(requestCount > 10 && shouldSlowDown(ipAddress, endPoint))
            slowDownRequest();

        System.out.println(rateLimiterMap);
    }

    private boolean shouldSlowDown(String ipAddress, String endPoint) {

        long currentTime = System.currentTimeMillis();
        Map<String, Long> endPointTimestamps = timestampMap.computeIfAbsent(ipAddress, k -> new HashMap<>());
        Long lastRequestTime = endPointTimestamps.get(endPoint);

        if (lastRequestTime != null) {
            long elapsedTime = currentTime - lastRequestTime;
            if (elapsedTime < 60000) {
                return true;
            } else {
                rateLimiterMap.get(ipAddress).get(endPoint).set(1);
            }
        }

        endPointTimestamps.put(endPoint, currentTime);
        return false;
    }


    private void slowDownRequest() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
