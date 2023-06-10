package com.deiaa.throttling.Throttle;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicRequestThrottlingStrategy implements ThrottleStrategy{

    private final Map<String, Map<String, AtomicInteger>> rateLimiterMap;
    private final Map<String, Map<String, Long>> timestampMap;
    private final AtomicInteger atomicInteger;

    public DynamicRequestThrottlingStrategy() {
        this.rateLimiterMap = new HashMap<>();
        timestampMap = new HashMap<>();
        atomicInteger = new AtomicInteger(0);

    }

    @Override
    public void throttleRequest(String ipAddress, String endPoint) throws InterruptedException {

        rateLimiterMap.computeIfAbsent(ipAddress, k -> new HashMap<>())
                .computeIfAbsent(endPoint, k -> new AtomicInteger(0))
                .incrementAndGet();

        int requestCount = rateLimiterMap.get(ipAddress).get(endPoint).get();

        if(requestCount >= 30 && shouldSlowDown(ipAddress, endPoint))
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
                rateLimiterMap.get(ipAddress).get(endPoint).set(0);
                atomicInteger.set(0);

            }
        }

        endPointTimestamps.put(endPoint, currentTime);
        return false;
    }


    private void slowDownRequest() throws InterruptedException {
        int currentCount = atomicInteger.incrementAndGet();
        System.out.println(currentCount);
        if (currentCount > 30) {
            atomicInteger.decrementAndGet();
            throw new InterruptedException("Service Unavailable");
        }

        Thread.sleep(3000);
        atomicInteger.decrementAndGet();

    }

}
