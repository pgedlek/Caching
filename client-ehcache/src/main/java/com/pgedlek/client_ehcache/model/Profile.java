package com.pgedlek.client_ehcache.model;

import java.io.Serializable;

public record Profile(
        Long profileId,
        String firstName,
        String middleName,
        String lastName,
        String email,
        String phoneNumber,
        String city) implements Serializable {
}