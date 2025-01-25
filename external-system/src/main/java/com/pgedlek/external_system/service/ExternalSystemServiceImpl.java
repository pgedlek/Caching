package com.pgedlek.external_system.service;

import com.pgedlek.external_system.model.Profile;
import com.pgedlek.external_system.repository.ExternalSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalSystemServiceImpl implements ExternalSystemService {
    private final ExternalSystemRepository externalSystemRepository;

    @Override
    public Mono<Profile> getProfile(Long profileId) {
        Optional<Profile> profileFromDb = externalSystemRepository.getProfileById(profileId);

        Mono<Profile> profile = profileFromDb.map(Mono::just).orElseGet(Mono::empty);

        // delay added to simulate retrieving from DB
        return profile.delayElement(Duration.of(1L, ChronoUnit.SECONDS));
    }
}
