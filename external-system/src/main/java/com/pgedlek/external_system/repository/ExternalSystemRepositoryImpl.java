package com.pgedlek.external_system.repository;

import com.pgedlek.external_system.model.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ExternalSystemRepositoryImpl implements ExternalSystemRepository {
    private static final List<Profile> db = new ArrayList<>(List.of(
            new Profile(1L, "John", "Paul", "Nowak", "jpnowak@email.com", "+48 123 456 789", "Krakow"),
            new Profile(2L, "Justin", "Peter", "Kowalski", "jpkowalski@email.eu", "+48 987 654 321", "Warsaw"),
            new Profile(3L, "Joseph", "Patrick", "Wisniewski", "jpwisniewski@email.pl", "+48 111 222 333", "Gdansk")
    ));

    private Integer id = db.size();

    @Override
    public List<Profile> getAllProfiles() {
        return db;
    }

    @Override
    public Optional<Profile> getProfileById(Long profileId) {
        return db.stream()
                .filter(profile -> Objects.equals(profileId, profile.getProfileId()))
                .findFirst();
    }

    @Override
    public Optional<Profile> addProfile(Profile profile) {
        if (profile.getProfileId() == null) {
            id += 1;
            profile.setProfileId(Long.valueOf(id));
        }

        if (db.stream().anyMatch(p -> profile.getProfileId().equals(p.getProfileId()))) {
            throw new IllegalArgumentException("Profile with id " + profile.getProfileId() + "already exist!");
        }

        db.add(profile);
        return Optional.of(profile);
    }

    @Override
    public Optional<Profile> updateProfile(Long profileId, Profile profile) {
        for (int i = 0; i < db.size(); i++) {
            Profile profileFromDb = db.get(i);
            if (Objects.equals(profileFromDb.getProfileId(), profileId)) {
                profile.setProfileId(profileFromDb.getProfileId());
                int index = db.indexOf(profileFromDb);
                db.set(index, profile);
                return Optional.of(profile);
            }
        }

        return Optional.empty();
    }
}
