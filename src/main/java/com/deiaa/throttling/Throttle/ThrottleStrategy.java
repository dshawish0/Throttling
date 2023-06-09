package com.deiaa.throttling.Throttle;

public interface ThrottleStrategy {
    void throttleRequest(String ipAddress, String endPoint) throws InterruptedException;
}
