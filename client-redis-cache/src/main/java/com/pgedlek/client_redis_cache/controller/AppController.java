package com.pgedlek.client_redis_cache.controller;

import com.pgedlek.client_redis_cache.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
    public void clearAllCache() {
        appService.clearAllCaches();
    }

    @GetMapping(value = "/profile/clear-cache/{profileId}")
    public void clearSpecificKey(@PathVariable Long profileId) {
        appService.clearSpecificKey(profileId);
    }
}
