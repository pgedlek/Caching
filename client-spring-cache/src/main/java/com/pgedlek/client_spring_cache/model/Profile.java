package com.pgedlek.client_spring_cache.model;

public record Profile(
        Long profileId,
        String firstName,
        String middleName,
        String lastName,
        String email,
        String phoneNumber,
        String city) {
}