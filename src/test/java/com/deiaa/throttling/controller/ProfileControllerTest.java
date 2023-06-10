package com.deiaa.throttling.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.deiaa.throttling.Throttle.ThrottleStrategy;
import com.deiaa.throttling.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class ProfileControllerTest {



    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ThrottleStrategy throttleStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetProfile_Success() throws InterruptedException {

        String expectedResponse = "http://localhost:8080/api/profile";
        when(profileService.getProfile()).thenReturn(expectedResponse);

        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/profile");

        doNothing().when(throttleStrategy).throttleRequest(anyString(), anyString());

        ProfileService mockProfileService = new ProfileService();
        mockProfileService.setThrottleStrategy();
        mockProfileService.setRequest(request);
        profileController.setProfileService(mockProfileService);

        ResponseEntity<String> response = profileController.getProfile();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void testGetProfile_DelayAfter10Requests() throws InterruptedException {

        String expectedResponse = "http://localhost:8080/api/profile";
        when(profileService.getProfile()).thenReturn(expectedResponse);

        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/profile");

        doNothing().when(throttleStrategy).throttleRequest(anyString(), anyString());

        ProfileService mockProfileService = new ProfileService();
        mockProfileService.setThrottleStrategy();
        mockProfileService.setRequest(request);
        profileController.setProfileService(mockProfileService);

        for (int i = 1; i <= 10; i++) {
            ResponseEntity<String> response = profileController.getProfile();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }

        long startTime = System.currentTimeMillis();
        ResponseEntity<String> delayedResponse = profileController.getProfile();
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        assertEquals(HttpStatus.OK, delayedResponse.getStatusCode());
        assertEquals(expectedResponse, delayedResponse.getBody());
        assertTrue(duration >= 3000);

    }

}
