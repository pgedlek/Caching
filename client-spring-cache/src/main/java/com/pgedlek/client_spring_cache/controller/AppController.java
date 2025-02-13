package com.pgedlek.client_spring_cache.controller;

import com.pgedlek.client_spring_cache.model.Profile;
import com.pgedlek.client_spring_cache.service.AppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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

    @PostMapping("/profile")
    public Mono<ResponseEntity<Profile>> addProfile(@RequestBody Profile profile) {
        return appService.addProfile(profile)
                .map(p -> new ResponseEntity<>(p, CREATED))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @PutMapping("/profile/{profileId}")
    public Mono<ResponseEntity<Profile>> updateProfile(@PathVariable Long profileId, @RequestBody Profile profile) {
        return appService.updateProfile(profileId, profile)
                .map(p -> new ResponseEntity<>(p, OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
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
