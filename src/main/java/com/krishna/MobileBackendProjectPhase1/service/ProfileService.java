package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.ProfileRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ProfileResponse;

public interface ProfileService {

    ProfileResponse createProfile(Long userId, ProfileRequest request);

    ProfileResponse getProfile(Long userId);
}