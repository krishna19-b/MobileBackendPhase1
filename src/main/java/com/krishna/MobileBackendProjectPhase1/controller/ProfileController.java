package com.krishna.MobileBackendProjectPhase1.controller;

import com.krishna.MobileBackendProjectPhase1.dto.request.ProfileRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ProfileResponse;
import com.krishna.MobileBackendProjectPhase1.service.ProfileService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class ProfileController {

    private final ProfileService profileService;
    public ProfileController(ProfileService profileService) {

        this.profileService = profileService;
    }


    @PostMapping("/{id}/profile")
    public ResponseEntity<ProfileResponse> createProfile(@PathVariable Long id, @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.createProfile(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}/profile")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long id) {

        return ResponseEntity.ok(profileService.getProfile(id));
    }
}