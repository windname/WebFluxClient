package com.vg.webflux.example;

import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import org.slf4j.Logger;


public class AllInOne {
    private static final Logger log = LoggerFactory.getLogger(AllInOne.class);

    public static void main(String[] args) throws InterruptedException {

        // creational
        Mono.just("Hello");
        Flux.just(1, 2, 3);
        Mono.empty();
        Flux.empty();
        Flux.fromIterable(List.of("A", "B"));
        Mono.fromCallable(() -> "Result");
        Flux.range(1, 5); // (emits 1, 2, 3, 4, 5)
        Flux.create(sink -> sink.next("Data")).subscribe(data -> System.out.println(data)); // manual flux with data
        Mono.defer(() -> Mono.just(LocalTime.now())); // (lazy)
        Mono.error(new RuntimeException());

        // transformation
        Flux.just(1, 2).map(i -> i * 2); // direct manipulation (2, 4)
        Flux.just(1, 2).flatMap(i -> Flux.range(1, i));
        Mono.just(3).flatMapMany(i -> Flux.range(1, i)); // flatten Mono<Flux> (1,2,3)
        Flux.just(1, 2).transform(flux -> flux.map(i -> i + 1));// transformation functio (2,3)

        // filtering
        Flux.range(1, 4).filter(i -> i % 2 == 0); // [2, 4]
        Flux.just(1, 2, 1).distinct(); // [1, 2]
        Flux.range(1, 5).take(2); // [1, 2]
        Flux.range(1, 5).skip(2); // [3, 4, 5]
        Flux.just(1, 2, 3, 0).takeWhile(i -> i < 3); // [1, 2]
        Flux.just(1, 2, 3, 0).skipWhile(i -> i < 3); // [3, 0]

        // Combination
        Flux.just(1).concatWith(Flux.just(2)); // [1, 2]
        Flux.just(1).mergeWith(Flux.just(2)); // [1, 2] (unordered merge)
        Flux.zip(Flux.just("A"), Flux.just(1), (a, b) -> a + b); // ["A1"]
        Flux.concat(Flux.just(1), Flux.just(2)); // [1, 2]
        Flux.combineLatest(Flux.just(1), Flux.just("A"), (a, b) -> a + b); // ["1A"]
        Flux.merge(Flux.just(1), Flux.just(2)); // [1, 2] (unordered)

        // errors
        Mono.error(new RuntimeException()).onErrorResume(e -> Mono.just("fallback")); // "fallback"
        Mono.error(new RuntimeException()).onErrorReturn("fallback"); // "fallback"
        Flux.error(new IOException("error message")).onErrorMap(e -> new RuntimeException(e)); // Throws RuntimeException
        Flux.error(new RuntimeException()).retry(1); // Retries once, then fails
        Flux.error(new RuntimeException()).retryWhen(Retry.max(2)); // Retries twice

        // Terminal Operations (Blocking/Subscribing)
        Flux.just(1, 2).subscribe(System.out::println); // Prints 1, 2
        Mono.just("data").block(); // Blocks and returns "data" (avoid in reactive code!)
        Flux.just(1, 2).blockFirst(); // Blocks and returns 1

        // Aggregation
        Flux.range(1, 3).count(); // Mono[3]
        Flux.range(1, 3).reduce((a, b) -> a + b); // Mono[6]
        Flux.just(1, 2).collectList(); // Mono[List[1, 2]]

        // Time & Side Effects
        Mono.delay(Duration.ofSeconds(2)).timeout(Duration.ofSeconds(1)); // Fails with TimeoutException
        Flux.just(1).doOnNext(i -> log.info("Got: " + i)); // Logs on emission
        Mono.error(new RuntimeException()).doOnError(e -> log.info("Failed!")); // Logs on error
        Flux.just(1).doOnTerminate(() -> log.info("Done!")); // Logs on completion/error
        Flux.just(1).log(); // Logs all signals (subscribe, next, complete)
        Flux.just(1, 2).delayElements(Duration.ofSeconds(1)); // Delays each element by 1s

        Thread.sleep(100);
    }
}
