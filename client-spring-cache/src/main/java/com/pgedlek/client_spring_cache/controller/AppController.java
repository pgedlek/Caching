package com.pgedlek.client_spring_cache.controller;

import com.pgedlek.client_spring_cache.service.AppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppController {
    private final AppService appService;

    @GetMapping(value = "/profile/{profileId}")
    public Mono<?> getProfile(@PathVariable Long profileId) {
        return appService.getProfile(profileId);
    }

    @GetMapping(value = "/profile/clear-cache")
    public Mono<String> clearAllCaches() {
        appService.clearAllCaches();
        return Mono.just("Cleared all caches successfully");
    }

    @GetMapping(value = "/profile/clear-cache/{profileId}")
    public Mono<String> clearSpecificKey(@PathVariable Long profileId) {
        appService.clearSpecificKey(profileId);
        return Mono.just(String.format("Cleared profile with id %d from cache successfully", profileId));
    }
}
