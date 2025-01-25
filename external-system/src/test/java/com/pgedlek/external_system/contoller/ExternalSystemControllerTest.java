package com.pgedlek.external_system.contoller;

import com.pgedlek.external_system.model.Profile;
import com.pgedlek.external_system.service.ExternalSystemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;


@WebFluxTest(controllers = ExternalSystemController.class)
public class ExternalSystemControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ExternalSystemService externalSystemServiceMock;

    @Test
    void shouldGetProfileSuccessfully() {
        // given
        Profile profile = new Profile(1L, "firstName", "middleName", "lastName", "email", "phoneNumber", "city");
        when(externalSystemServiceMock.getProfile(1L)).thenReturn(Mono.just(profile));

        // when + then
        webClient.get()
                .uri("/external-system/profile/{profileId}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Profile.class).isEqualTo(profile);
    }

    @Test
    void shouldReturnNotFoundStatusWhenCannotFindProfile() {
        // given
        when(externalSystemServiceMock.getProfile(1L)).thenReturn(Mono.empty());

        // when + then
        webClient.get()
                .uri("/external-system/profile/{profileId}", 1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}