package com.pgedlek.client_ehcache.controller;

import com.pgedlek.client_ehcache.model.Profile;
import com.pgedlek.client_ehcache.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppController {
    private final AppService appService;
    private final CacheManager cacheManager;

    @GetMapping(value = "/profile/{profileId}")
    public ResponseEntity<?> getProfile(@PathVariable Long profileId) {
        Optional<Profile> profile = appService.getProfile(profileId);

        return profile.isPresent()
                ? new ResponseEntity<>(profile, OK)
                : new ResponseEntity<>("Cannot find profile with ID " + profileId, NOT_FOUND);
    }

    @GetMapping(value = "/profile/clear-cache")
    public void clearAllCache() {
        Cache<Object, Object> profileCache = cacheManager.getCache("profile");
        profileCache.removeAll();
    }

    @GetMapping(value = "/profile/clear-cache/{profileId}")
    public void clearSpecificKey(@PathVariable Long profileId) {
        Cache<Object, Object> profileCache = cacheManager.getCache("profile");
        profileCache.remove(profileId);
    }
}