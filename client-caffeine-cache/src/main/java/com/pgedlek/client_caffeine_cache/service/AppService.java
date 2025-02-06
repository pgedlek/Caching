package com.pgedlek.client_caffeine_cache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pgedlek.client_caffeine_cache.model.Profile;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PathVariable;
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
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
    private final ScheduledExecutorService scheduler;

    public AppService() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refreshCache, 0, 1, TimeUnit.MINUTES);
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

    public void clearAllCaches() {
        caffeineCache.invalidateAll();
    }

    public void clearSpecificKey(@PathVariable Long profileId) {
        caffeineCache.invalidate(profileId);
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
