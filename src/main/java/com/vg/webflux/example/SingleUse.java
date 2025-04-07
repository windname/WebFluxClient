package com.vg.webflux.example;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class SingleUse {
    public static void main(String[] args) {
        // single use flux
        Flux<String> webFlux = WebClient.create().get()
                .uri("https://google.com")
                .retrieve()
                .bodyToFlux(String.class);

        webFlux.subscribe();  // OK
        webFlux.subscribe();  // ❌ Throws "This publisher can only be subscribed to once"
    }
}
