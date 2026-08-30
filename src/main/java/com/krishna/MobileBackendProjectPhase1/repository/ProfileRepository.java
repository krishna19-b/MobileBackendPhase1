package com.krishna.MobileBackendProjectPhase1.repository;

import com.krishna.MobileBackendProjectPhase1.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository
        extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);
}