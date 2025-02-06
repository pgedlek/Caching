package com.pgedlek.external_system.contoller;

import com.pgedlek.external_system.model.Profile;
import com.pgedlek.external_system.service.ExternalSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/external-system")
@RequiredArgsConstructor
public class ExternalSystemController {
    private final ExternalSystemService externalSystemService;

    @GetMapping("/profile/{profileId}")
    public Mono<ResponseEntity<Profile>> getProfile(@PathVariable Long profileId) {
        return externalSystemService.getProfile(profileId)
                .map(profile -> new ResponseEntity<>(profile, OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/profile")
    public Mono<ResponseEntity<Profile>> addProfile(@RequestBody Profile profile) {
        return externalSystemService.addProfile(profile)
                .map(p -> new ResponseEntity<>(p, CREATED))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @PutMapping("/profile/{profileId}")
    public Mono<ResponseEntity<Profile>> updateProfile(@PathVariable Long profileId, @RequestBody Profile profile) {
        return externalSystemService.updateProfile(profileId, profile)
                .map(p -> new ResponseEntity<>(p, CREATED))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }
}
