package com.vg.webflux.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@WebFluxTest(HelloWorldController.class)
class HelloWorldControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testEndpoint() {
        webTestClient.get()
                .uri("/hello")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .isEqualTo(List.of("Hello, World!"));
    }
}