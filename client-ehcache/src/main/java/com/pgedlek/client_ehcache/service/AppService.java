package com.pgedlek.client_ehcache.service;

import com.pgedlek.client_ehcache.model.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppService {
    private final CacheManager cacheManager;

    private static final String URL = "http://localhost:8888/external-system/profile";

    @Cacheable(value = "profile", key = "#profileId")
    public Optional<Profile> getProfile(Long profileId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Profile> response = restTemplate.getForEntity(URL + "/" + profileId, Profile.class);

        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return Optional.of(response.getBody());
        }

        return Optional.empty();
    }

    public Optional<Profile> addProfile(Profile profile) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Profile> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<Profile>(profile), Profile.class);

        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            Profile createdProfile = response.getBody();
            cacheManager.getCache("profile").put(createdProfile.profileId(), createdProfile);
            return Optional.of(createdProfile);
        }

        return Optional.empty();
    }

    @CachePut(value = "profile", key = "#profileId")
    public Optional<Profile> updateProfile(Long profileId, Profile profile) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Profile> response = restTemplate.exchange(URL + "/" + profileId, HttpMethod.PUT, new HttpEntity<Profile>(profile), Profile.class);

        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return Optional.of(response.getBody());
        }

        return Optional.empty();
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
