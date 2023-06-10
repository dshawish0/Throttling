package com.deiaa.throttling.controller;

import com.deiaa.throttling.Throttle.ThrottleStrategy;
import com.deiaa.throttling.service.HistoryService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HistoryControllerTest {

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private HistoryController historyController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ThrottleStrategy throttleStrategy;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetHistory_Success() throws InterruptedException {

        String expectedResponse = "http://localhost:8080/api/history";
        when(historyService.getHistory()).thenReturn(expectedResponse);

        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/history");

        doNothing().when(throttleStrategy).throttleRequest(anyString(), anyString());

        HistoryService mockHistoryService = new HistoryService();
        mockHistoryService.setThrottleStrategy();
        mockHistoryService.setRequest(request);
        historyController.setHistoryService(mockHistoryService);

        ResponseEntity<String> response = historyController.getHistory();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testGetHistory_ThrottlingException() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(60);
        List<Future<?>> futures = new ArrayList<>();


        String expectedResponse = "http://localhost:8080/api/history";
        when(historyService.getHistory()).thenReturn(expectedResponse);

        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/history");

        doNothing().when(throttleStrategy).throttleRequest(anyString(), anyString());

        HistoryService mockHistoryService = new HistoryService();
        mockHistoryService.setThrottleStrategy();
        mockHistoryService.setRequest(request);
        historyController.setHistoryService(mockHistoryService);

        for (int i = 1; i <= 61; i++) {
            futures.add(executorService.submit(() -> {
                ResponseEntity<String> response = historyController.getHistory();
                if (response.getStatusCode().equals(HttpStatus.OK))
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                else
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
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


    @Test
    public void testGetHistory_DelayAfter30Requests() throws InterruptedException {

        String expectedResponse = "http://localhost:8080/api/history";
        when(historyService.getHistory()).thenReturn(expectedResponse);

        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/history");

        doNothing().when(throttleStrategy).throttleRequest(anyString(), anyString());

        HistoryService mockHistoryService = new HistoryService();
        mockHistoryService.setThrottleStrategy();
        mockHistoryService.setRequest(request);
        historyController.setHistoryService(mockHistoryService);

        for (int i = 1; i <= 30; i++) {
            ResponseEntity<String> response = historyController.getHistory();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }
        long startTime = System.currentTimeMillis();
        ResponseEntity<String> delayedResponse = historyController.getHistory();
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        assertEquals(HttpStatus.OK, delayedResponse.getStatusCode());
        assertEquals(expectedResponse, delayedResponse.getBody());
        assertTrue(duration >= 3000);
    }
}
