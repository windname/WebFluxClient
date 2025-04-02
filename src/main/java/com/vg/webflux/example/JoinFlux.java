package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class JoinFlux {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> nums = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(10));
        Flux<String> letters = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(15));

        Flux<Tuple2<Integer, String>> joined = nums.join(letters,
                num -> Flux.never(), // Window stays open for nums
                letter -> Flux.never(), // Window stays open for letters
                Tuples::of
        );

        joined.subscribe(tuple -> System.out.println(tuple.getT1() + "-" + tuple.getT2()));

        // Simulate two asynchronous streams
        Flux<String> userActions = Flux.just("Click", "Scroll", "Type")
                .delayElements(Duration.ofMillis(200));

        Flux<Integer> mousePositions = Flux.just(100, 200, 300)
                .delayElements(Duration.ofMillis(300));

        // Combine latest emissions
        Flux.combineLatest(
                userActions,
                mousePositions,
                (action, pos) -> action + "@" + pos
        ).subscribe(System.out::println);

        // Prevent main thread from exiting immediately
        TimeUnit.SECONDS.sleep(1);
    }
}
