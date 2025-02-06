package com.pgedlek.external_system.repository;

import com.pgedlek.external_system.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ExternalSystemRepository {
    List<Profile> getAllProfiles();

    Optional<Profile> getProfileById(Long profileId);

    Optional<Profile> addProfile(Profile profile);

    Optional<Profile> updateProfile(Long profileId, Profile profile);
}
