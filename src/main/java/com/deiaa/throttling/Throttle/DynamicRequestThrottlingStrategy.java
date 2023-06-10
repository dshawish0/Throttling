package com.deiaa.throttling.Throttle;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicRequestThrottlingStrategy implements ThrottleStrategy{

    private final Map<String, Map<String, AtomicInteger>> rateLimiterMap;
    private final Map<String, Map<String, Long>> timestampMap;
    private final Map <String, Map<String, AtomicInteger>> throttledRequests;

    public DynamicRequestThrottlingStrategy() {
        this.rateLimiterMap = new HashMap<>();
        this.timestampMap = new HashMap<>();
        this.throttledRequests = new HashMap<>();

    }

    @Override
    public void throttleRequest(String ipAddress, String endPoint) throws InterruptedException {

        rateLimiterMap.computeIfAbsent(ipAddress, k -> new HashMap<>())
                .computeIfAbsent(endPoint, k -> new AtomicInteger(0))
                .incrementAndGet();

        throttledRequests.computeIfAbsent(ipAddress, k -> new HashMap<>())
                .computeIfAbsent(endPoint, k -> new AtomicInteger(0));

        int requestCount = rateLimiterMap.get(ipAddress).get(endPoint).get();

        if(requestCount >= 30 && shouldSlowDown(ipAddress, endPoint))
            slowDownRequest(ipAddress, endPoint);

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
                rateLimiterMap.get(ipAddress).get(endPoint).set(0);
                throttledRequests.get(ipAddress).get(endPoint).set(0);

            }
        }

        endPointTimestamps.put(endPoint, currentTime);
        return false;
    }


    private void slowDownRequest(String ipAddress, String endPoint) throws InterruptedException {
        int currentCount = throttledRequests.get(ipAddress).get(endPoint).incrementAndGet();
        System.out.println(currentCount);
        if (currentCount > 30) {
            throttledRequests.get(ipAddress).get(endPoint).decrementAndGet();
            throw new InterruptedException("Service Unavailable");
        }

        Thread.sleep(3000);
        throttledRequests.get(ipAddress).get(endPoint).decrementAndGet();

    }

}
