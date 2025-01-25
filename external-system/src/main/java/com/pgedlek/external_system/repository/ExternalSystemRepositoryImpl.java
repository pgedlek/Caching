package com.pgedlek.external_system.repository;

import com.pgedlek.external_system.model.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ExternalSystemRepositoryImpl implements ExternalSystemRepository {
    private static final List<Profile> db = List.of(
            new Profile(1L, "John", "Paul", "Nowak", "jpnowak@email.com", "+48 123 456 789", "Krakow"),
            new Profile(2L, "Justin", "Peter", "Kowalski", "jpkowalski@email.eu", "+48 987 654 321", "Warsaw"),
            new Profile(3L, "Joseph", "Patrick", "Wisniewski", "jpwisniewski@email.pl", "+48 111 222 333", "Gdansk")
    );

    @Override
    public Optional<Profile> getProfileById(Long profileId) {
        return db.stream()
                .filter(profile -> Objects.equals(profileId, profile.getProfileId()))
                .findFirst();
    }
}
