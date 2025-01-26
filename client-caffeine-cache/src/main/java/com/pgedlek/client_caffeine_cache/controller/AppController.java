package com.pgedlek.client_caffeine_cache.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pgedlek.client_caffeine_cache.model.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api")
public class AppController {
    private static final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8888/external-system")
            .build();
    private static final Cache<Long, Profile> caffeineCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofHours(24))
            .build();

    @GetMapping(value = "/profile/{profileId}")
    public Mono<?> getProfile(@PathVariable Long profileId) {
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
                        .doOnNext(retrivedProfile -> caffeineCache.put(profileId, retrivedProfile))
                        .onErrorResume(e -> {
                            log.error("Error fetching profile: " + e.getMessage());
                            return Mono.empty();
                        });
            }
        });
    }

    @GetMapping(value = "/profile/clear-cache")
    public void clearAllCache() {
        caffeineCache.invalidateAll();
    }

    @GetMapping(value = "/profile/clear-cache/{profileId}")
    public void clearSpecificKey(@PathVariable Long profileId) {
        caffeineCache.invalidate(profileId);
    }
}
