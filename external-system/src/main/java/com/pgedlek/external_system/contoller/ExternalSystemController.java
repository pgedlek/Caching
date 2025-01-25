package com.pgedlek.external_system.contoller;

import com.pgedlek.external_system.model.Profile;
import com.pgedlek.external_system.service.ExternalSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/external-system")
@RequiredArgsConstructor
public class ExternalSystemController {
    private final ExternalSystemService externalSystemService;

    @GetMapping("/profile/{profileId}")
    public Mono<Profile> getProfile(@PathVariable Long profileId) {
        return externalSystemService.getProfile(profileId);
    }
}
