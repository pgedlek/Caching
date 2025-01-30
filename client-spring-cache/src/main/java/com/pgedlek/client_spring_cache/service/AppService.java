package com.pgedlek.client_spring_cache.service;

import com.pgedlek.client_spring_cache.model.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AppService {
    private static final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8888/external-system")
            .build();

    @Cacheable(value = "profiles")
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

    @CacheEvict(value = "profiles")
    public void clearAllCaches() {
        log.info("Removed all caches");
    }

    @CacheEvict(value = "profiles", key = "#profileId")
    public void clearSpecificKey(Long profileId) {
        log.info("Remove profile with ID {} from cache", profileId);
    }
}
