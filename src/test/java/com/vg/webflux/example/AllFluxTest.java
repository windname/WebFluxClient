package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class AllFluxTest {

    @Test
    void testFluxMap() {
        Flux<String> flux = Flux.just("apple", "banana", "cherry")
                .map(String::toUpperCase);

        StepVerifier.create(flux)
                .expectNext("APPLE")
                .expectNext("BANANA")
                .expectNext("CHERRY")
                .verifyComplete();
    }

    @Test
    void testFluxFilter() {
        Flux<Integer> flux = Flux.range(1, 10)
                .filter(i -> i % 2 == 0);

        StepVerifier.create(flux)
                .expectNext(2, 4, 6, 8, 10)
                .verifyComplete();
    }

    @Test
    void testFluxFlatMap() {
        Flux<String> flux = Flux.just("apple", "banana")
                .flatMap(s -> Flux.fromArray(s.split("")));

        StepVerifier.create(flux)
                .expectNext("a", "p", "p", "l", "e", "b", "a", "n", "a", "n", "a")
                .verifyComplete();
    }

    @Test
    void testFluxConcatWith() {
        Flux<String> flux1 = Flux.just("A", "B");
        Flux<String> flux2 = Flux.just("C", "D");

        StepVerifier.create(flux1.concatWith(flux2))
                .expectNext("A", "B", "C", "D")
                .verifyComplete();
    }

    @Test
    void testFluxZip() {
        Flux<String> flux1 = Flux.just("A", "B");
        Flux<String> flux2 = Flux.just("1", "2");

        StepVerifier.create(Flux.zip(flux1, flux2, (s1, s2) -> s1 + s2))
                .expectNext("A1", "B2")
                .verifyComplete();
    }

    @Test
    void testFluxErrorHandling() {
        Flux<Integer> flux = Flux.just(1, 2, 0)
                .map(i -> 10 / i)
                .onErrorResume(e -> Flux.just(-1));

        StepVerifier.create(flux)
                .expectNext(10)
                .expectNext(5)
                .expectNext(-1)
                .verifyComplete();
    }

    @Test
    void testFluxTake() {
        Flux<Integer> flux = Flux.range(1, 100)
                .take(3);

        StepVerifier.create(flux)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void testMonoJust() {
        Mono<String> mono = Mono.just("hello");

        StepVerifier.create(mono)
                .expectNext("hello")
                .verifyComplete();
    }

    @Test
    void testMonoError() {
        Mono<String> mono = Mono.error(new RuntimeException("error"));

        StepVerifier.create(mono)
                .verifyError(RuntimeException.class);
    }

    @Test
    void testMonoFlatMap() {
        Mono<String> mono = Mono.just("hello")
                .flatMap(s -> Mono.just(s + " world"));

        StepVerifier.create(mono)
                .expectNext("hello world")
                .verifyComplete();
    }

    @Test
    void testMonoZip() {
        Mono<String> mono1 = Mono.just("Hello");
        Mono<String> mono2 = Mono.just("World");

        StepVerifier.create(Mono.zip(mono1, mono2, (s1, s2) -> s1 + " " + s2))
                .expectNext("Hello World")
                .verifyComplete();
    }

    @Test
    void testVerifySubscription() {
        Flux<Integer> flux = Flux.range(1, 3);

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void testVerifyDuration() {
        Flux<Integer> flux = Flux.range(1, 3).delayElements(Duration.ofMillis(100));

        StepVerifier.create(flux)
                .expectNext(1, 2, 3)
                .expectComplete()
                .verify(Duration.ofSeconds(1));
    }

    @Test
    void testWithVirtualTime() {
        StepVerifier.withVirtualTime(() ->
                        Flux.interval(Duration.ofHours(1)).take(2))
                .expectSubscription()
                .thenAwait(Duration.ofHours(2))
                .expectNext(0L, 1L)
                .verifyComplete();
    }

    @Test
    void testVerifyErrorMessage() {
        Mono<String> mono = Mono.error(new IllegalArgumentException("Invalid argument"));

        StepVerifier.create(mono)
                .verifyErrorMatches(e ->
                        e instanceof IllegalArgumentException &&
                                e.getMessage().equals("Invalid argument"));
    }

    record User(
            String name,      // User's full name
            int age) {
        User(String name, int age) {
            this.name = name;
            this.age = age;
        }

    }

    @Test
    void testUserObjectProperties() {
        Flux<User> userFlux = Flux.just(
                new User("Alice", 25),
                new User("Bob", 30)
        );

        StepVerifier.create(userFlux)
                .expectNextMatches(user ->
                        user.name.equals("Alice") &&
                                user.age == 25)
                .expectNextMatches(user ->
                        user.name.startsWith("B") &&
                                user.age > 25)
                .verifyComplete();
    }

    @Test
    void testBackpressureWithThenRequest() {
        Flux<User> userFlux = Flux.range(1, 5)
                .map(i -> new User("User_" + i, 20 + i))
                .log(); // Log to see request signals

        StepVerifier.create(userFlux)
                .expectSubscription() // Verify subscription first
                .thenRequest(1) // Request 1 item
                .expectNextMatches(user -> user.name().startsWith("User_1"))
                .thenRequest(2) // Request 2 more items
                .expectNextCount(2) // Verify 2 items without checking values
                .thenRequest(Long.MAX_VALUE) // Request all remaining
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testStreamCancellation() {
        Flux<User> infiniteUsers = Flux.interval(Duration.ofMillis(100))
                .map(i -> new User("Infinite_" + i, 30));

        StepVerifier.create(infiniteUsers)
                .expectSubscription()
                .thenRequest(1)
                .expectNextMatches(user -> user.name().startsWith("Infinite_0"))
                .thenCancel() // Actively cancel the subscription
                .verify(); // No verifyComplete() since we canceled
    }

    @Test
    void testDelayedUserEmission() {
        Flux<User> delayedUsers = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .map(i -> new User("Delayed_" + i, 25 + i.intValue()));

        StepVerifier.withVirtualTime(() -> delayedUsers)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(1)) // Fast-forward 1 second
                .expectNextMatches(user ->
                        user.age() == 25 &&
                                user.name().equals("Delayed_0"))
                .thenAwait(Duration.ofSeconds(2)) // Skip 2 seconds
                .expectNextCount(2) // Remaining 2 users
                .verifyComplete();
    }

    @Test
    void testCombinedScenario() {
        Flux<User> controlledFlux = Flux.interval(Duration.ofMillis(500))
                .map(i -> new User("Ctrl_" + i, 18 + i.intValue()))
                .take(10);

        StepVerifier.withVirtualTime(() -> controlledFlux)
                .expectSubscription()
                .thenRequest(1) // Initial request
                .thenAwait(Duration.ofMillis(500)) // Advance to first emission
                .expectNextMatches(user -> user.age() == 18)
                .thenRequest(3) // Request 3 more
                .thenAwait(Duration.ofSeconds(2)) // Advance 2 seconds (4 items)
                .expectNextCount(3)
                .thenCancel() // Cancel remaining
                .verify();
    }




}
