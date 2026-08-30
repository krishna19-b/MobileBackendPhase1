package com.krishna.MobileBackendProjectPhase1.dto.response;

import com.krishna.MobileBackendProjectPhase1.entity.Profile;

public class ProfileResponse {

    private Long id;
    private String dateOfBirth;
    private String gender;
    private String bio;
    private Long userId;


    public ProfileResponse(Profile profile) {

        this.id = profile.getId();
        this.dateOfBirth = profile.getDateOfBirth();
        this.gender = profile.getGender();
        this.bio = profile.getBio();

        if (profile.getUser() != null) {
            this.userId = profile.getUser().getId();
        }
    }


    public Long getId() {
        return id;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getBio() {
        return bio;
    }

    public Long getUserId() {
        return userId;
    }
}