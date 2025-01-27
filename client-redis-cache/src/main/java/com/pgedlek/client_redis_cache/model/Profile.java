package com.pgedlek.client_redis_cache.model;

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
