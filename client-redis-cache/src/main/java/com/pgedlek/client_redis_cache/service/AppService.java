package com.pgedlek.client_redis_cache.service;

import com.pgedlek.client_redis_cache.model.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
public class AppService {
    private static final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8888/external-system")
            .build();

    private ReactiveRedisTemplate<Long, Profile> reactiveRedisTemplate;
    private ReactiveValueOperations<Long, Profile> valueOps;

    public AppService(ReactiveRedisTemplate<Long, Profile> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.valueOps = reactiveRedisTemplate.opsForValue();
    }

    public Mono<?> getProfile(Long profileId) {
        log.info("Get profile with ID: " + profileId);
        return valueOps.get(profileId)
                .switchIfEmpty(Mono.defer(() -> webClient.get()
                        .uri("/profile/{profileId}", profileId)
                        .retrieve()
                        .bodyToMono(Profile.class)
                        .flatMap(profile -> valueOps.set(profileId, profile, Duration.ofHours(24))
                                .thenReturn(profile))))
                .onErrorResume(e -> {
                    log.error("Error fetching profile: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    public void clearSpecificKey(Long profileId) {
        log.info("Clear profile from cache with ID: " + profileId);
        reactiveRedisTemplate.delete(profileId);
    }

    public void clearAllCaches() {
        log.info("Clear all caches.");
        reactiveRedisTemplate.getConnectionFactory().getReactiveConnection().serverCommands().flushAll();
    }
}
