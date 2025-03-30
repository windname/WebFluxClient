package com.vg.webflux.example;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class FluxBlocking {
    public static void main(String[] args) {

        // Using blockLast() (for testing/simple cases)
        Flux.range(1, 5)
                .delayElements(Duration.ofMillis(100))
                .doOnNext(i -> System.out.print(i + ", "))
                .blockLast();
        System.out.println("");

        // Mono + then() (cleaner approach) Convert flux to empty Mono (method then) and wait his end
        Mono<Void> completionSignal = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(100))
                .doOnNext(i -> System.out.print(i + ", "))
                .then();
        completionSignal.block(); // Blocks until completion
        System.out.println("");


        // multiple fluxes
        Flux<Integer> flux1 = Flux.range(1, 3).delayElements(Duration.ofMillis(100));
        Flux<Integer> flux2 = Flux.range(4, 3).delayElements(Duration.ofMillis(150));
        Mono.when(flux1.then(), flux2.then())
                .doOnNext(i -> System.out.print(i + ", "))
                .block();
        System.out.println("");


        // countdown latch
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Flux.range(1, 5)
                    .delayElements(Duration.ofMillis(100))
                    .doOnNext(i -> System.out.println("Processing: " + i))
                    .doOnComplete(latch::countDown)
                    .subscribe();
            latch.await();
        } catch (Exception ex) {}

        WebClient webClient = WebClient.create().mutate().build();
        // 2. Create the reactive pipeline
        Mono<Void> using = Flux.using(
                () -> webClient, // Resource supplier
                client ->
                        // Explicitly return Mono<Void> by adding then()
                        Flux.range(1, 3)
                                .flatMap(i -> makeRequest(client, "https://google.com"))
                                .doOnNext(response -> System.out.println("Received: " + response)),
                client -> {
                } // No cleanup needed for WebClient
        ).then();

        // 3. Block to wait for completion (for demonstration only)
        using.block(Duration.ofSeconds(5));

    }

    static class Metrics {
        void increment(String metricName) {
            System.out.println("[Metrics] Incremented: " + metricName);
        }
    }

    private static Mono<String> makeRequest(WebClient client, String endpoint) {
        return client.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2));
    }
}
