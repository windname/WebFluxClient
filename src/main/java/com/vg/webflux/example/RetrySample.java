package com.vg.webflux.example;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class RetrySample {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger attempt = new AtomicInteger();

        Mono<String> operation = Mono.defer(() -> {
            int current = attempt.incrementAndGet();
            System.out.println("Attempt #" + current);

            if (current < 3) {
                return Mono.error(new RuntimeException("Simulated failure"));
            } else {
                return Mono.just("Success on attempt #" + current);
            }
        });

        operation
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100))
                        .filter(throwable -> throwable instanceof RuntimeException))
                .subscribe(
                        result -> System.out.println("Result: " + result),
                        error -> System.err.println("Final error: " + error.getMessage())
                );

        // Give time for retries to complete (since we're not blocking)
        Thread.sleep(1000);
    }
}

