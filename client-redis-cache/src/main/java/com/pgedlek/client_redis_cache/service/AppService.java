package com.pgedlek.client_redis_cache.service;

import com.pgedlek.client_redis_cache.model.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppService {
    private final CacheManager cacheManager;

    private static final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8888/external-system")
            .build();

    @Cacheable(value = "profile", key = "#profileId")
    public Mono<?> getProfile(Long profileId) {
        return webClient.get()
                .uri("/profile/{profileId}", profileId)
                .retrieve()
                .bodyToMono(Profile.class)
                .onErrorResume(e -> {
                    log.error("Error fetching profile: " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Profile> addProfile(Profile profile) {
        return webClient.post()
                .uri("/profile", profile)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(profile))
                .retrieve()
                .bodyToMono(Profile.class)
                .doOnNext(createdProfile -> cacheManager.getCache("profile")
                        .put(createdProfile.profileId(), createdProfile))
                .onErrorResume(e -> {
                    log.error("Error adding profile: " + e.getMessage());
                    return Mono.empty();
                });
    }

    @CachePut(value = "profile", key = "#profileId")
    public Mono<Profile> updateProfile(Long profileId, Profile profile) {
        return webClient.put()
                .uri("/profile/{profileId}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(profile))
                .retrieve()
                .bodyToMono(Profile.class)
                .onErrorResume(e -> {
                    log.error("Error updating profile: " + e.getMessage());
                    return Mono.empty();
                });
    }

    @CacheEvict(value = "profile", allEntries = true)
    public void clearAllCaches() {
        log.info("Removed all caches");
    }

    @CacheEvict(value = "profile", key = "#profileId")
    public void clearSpecificKey(Long profileId) {
        log.info("Remove profile with ID {} from cache", profileId);
    }
}
