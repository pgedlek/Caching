package com.pgedlek.external_system.service;

import com.pgedlek.external_system.model.Profile;
import reactor.core.publisher.Mono;

public interface ExternalSystemService {
    Mono<Profile> getProfile(Long profileId);
}
