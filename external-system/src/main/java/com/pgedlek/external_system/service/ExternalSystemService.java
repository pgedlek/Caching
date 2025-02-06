package com.pgedlek.external_system.service;

import com.pgedlek.external_system.model.Profile;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ExternalSystemService {
    Mono<List<Profile>> getAllProfiles();

    Mono<Profile> getProfile(Long profileId);

    Mono<Profile> addProfile(Profile profile);

    Mono<Profile> updateProfile(Long profileId, Profile profile);
}
