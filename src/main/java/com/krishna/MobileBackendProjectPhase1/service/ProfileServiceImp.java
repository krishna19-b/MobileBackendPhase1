package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.ProfileRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ProfileResponse;
import com.krishna.MobileBackendProjectPhase1.entity.Profile;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.UserNotFoundException;
import com.krishna.MobileBackendProjectPhase1.repository.ProfileRepository;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImp implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;


    public ProfileServiceImp(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProfileResponse createProfile(Long userId, ProfileRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        if (profileRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Profile already exists for user: " + userId);
        }
        Profile profile = new Profile();
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setBio(request.getBio());
        profile.setUser(user);
        Profile savedProfile = profileRepository.save(profile);
        return new ProfileResponse(savedProfile);
    }


    @Override
    public ProfileResponse getProfile(Long userId) {

        Profile profile = profileRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        return new ProfileResponse(profile);
    }
}