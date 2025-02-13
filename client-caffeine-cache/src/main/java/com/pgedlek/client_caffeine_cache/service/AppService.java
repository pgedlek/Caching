package com.pgedlek.client_caffeine_cache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pgedlek.client_caffeine_cache.model.Profile;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AppService {
    private static final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8888/external-system")
            .build();
    private static final Cache<Long, Profile> caffeineCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build();
    private final ScheduledExecutorService scheduler;

    public AppService() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refreshCache, 60, 60, TimeUnit.MINUTES);
    }

    public Mono<Profile> getProfile(Long profileId) {
        return Mono.defer(() -> {
            Profile cachedProfile = caffeineCache.getIfPresent(profileId);
            if (cachedProfile != null) {
                log.info("Get profile from cache");
                return Mono.just(cachedProfile);
            } else {
                log.info("Get profile from external system");
                return webClient.get()
                        .uri("/profile/{profileId}", profileId)
                        .retrieve()
                        .bodyToMono(Profile.class)
                        .doOnNext(retrievedProfile -> caffeineCache.put(profileId, retrievedProfile))
                        .onErrorResume(e -> {
                            log.error("Error fetching profile: " + e.getMessage());
                            return Mono.empty();
                        });
            }
        });
    }

    public Mono<Profile> addProfile(Profile profile) {
        return webClient.post()
                .uri("/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(profile))
                .retrieve()
                .bodyToMono(Profile.class)
                .doOnNext(createdProfile -> caffeineCache.put(createdProfile.profileId(), createdProfile))
                .onErrorResume(e -> {
                    log.error("Error adding profile: " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Profile> updateProfile(Long profileId, Profile profile) {
        return webClient.put()
                .uri("/profile/{profileId}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(profile))
                .retrieve()
                .bodyToMono(Profile.class)
                .doOnNext(updatedProfile -> {
                    caffeineCache.invalidate(profileId);
                    caffeineCache.put(profileId, updatedProfile);
                })
                .onErrorResume(e -> {
                    log.error("Error updating profile: " + e.getMessage());
                    return Mono.empty();
                });
    }

    public void clearAllCaches() {
        caffeineCache.invalidateAll();
    }

    public void clearSpecificKey(Long profileId) {
        caffeineCache.invalidate(profileId);
    }

    @PostConstruct
    public void initCache() {
        log.info("Init cache");
        loadInitialData();
    }

    private void refreshCache() {
        log.info("Refresh");
        loadInitialData();
    }

    private void loadInitialData() {
        log.info("Load data");
        webClient.get()
                .uri("/profiles")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Profile>>() {
                })
                .doOnNext(profiles -> profiles.forEach(p -> caffeineCache.put(p.profileId(), p)))
                .onErrorResume(e -> {
                    log.error("Error fetching profile: " + e.getMessage());
                    return Mono.empty();
                }).block();
    }

    @PreDestroy
    private void shutdown() {
        log.info("Shutdown");
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }
}
