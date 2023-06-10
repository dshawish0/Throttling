package com.deiaa.throttling.controller;

import com.deiaa.throttling.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<String> getProfile() throws InterruptedException {
        return ResponseEntity.ok(profileService.getProfile());
    }

    public void setProfileService(ProfileService mockProfileService) {
        this.profileService = mockProfileService;
    }
}
