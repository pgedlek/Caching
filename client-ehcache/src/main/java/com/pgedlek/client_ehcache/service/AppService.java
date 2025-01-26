package com.pgedlek.client_ehcache.service;

import com.pgedlek.client_ehcache.model.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
public class AppService {
    private static final String URL = "http://localhost:8888/external-system/profile/";

    @Cacheable(value = "profile", key = "#profileId")
    public Optional<Profile> getProfile(Long profileId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Profile> response = restTemplate.getForEntity(URL + profileId, Profile.class);

        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return Optional.of(response.getBody());
        }

        return Optional.empty();
    }
}
