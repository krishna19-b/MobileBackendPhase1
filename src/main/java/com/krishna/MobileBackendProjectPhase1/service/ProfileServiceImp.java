package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.ProfileRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ProfileResponse;
import com.krishna.MobileBackendProjectPhase1.entity.Profile;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.UserNotFoundException;
import com.krishna.MobileBackendProjectPhase1.repository.ProfileRepository;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImp implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileServiceImp(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ProfileResponse createProfile(Long userId, ProfileRequest request) {
        User loggedInUser = getLoggedInUser();

        if (hasRole("USER") && !loggedInUser.getId().equals(userId)) {
            throw new AccessDeniedException("Users can create profiles only for themselves");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

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
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User loggedInUser = getLoggedInUser();

        if (hasRole("USER") && !loggedInUser.getId().equals(userId)) {
            throw new AccessDeniedException("Users can view only their own profile");
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found for user: " + userId));

        return new ProfileResponse(profile);
    }

    private User getLoggedInUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid authenticated user");
        }

        return (User) principal;
    }

    private boolean hasRole(String role) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_" + role));
    }
}