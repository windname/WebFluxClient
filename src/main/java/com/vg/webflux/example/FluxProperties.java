package com.vg.webflux.example;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class FluxProperties {
    public static void main(String[] args) throws InterruptedException {

        // cold flux - copy per subscriber
        Flux<Integer> shared = Flux.just(1, 2);
        shared.subscribe(System.out::println);
        shared.subscribe(System.out::println);

        // hot subscriber 1,2,3,4,4,5,5,6,6,7,7,8,8,9,9
        Flux<Integer> hot = Flux.range(1, 10).delayElements(Duration.ofMillis(100)).share();
        hot.subscribe(System.out::println);  // Subscriber 1
        Thread.sleep(400);
        hot.subscribe(System.out::println);  // Subscriber 2 (shares the same data stream)

//        fluxCanBeInfinite(1000);

        Thread.sleep(1200);

    }

    public static void fluxCanBeInfinite(int ttl) {
        Disposable disposable = Flux.interval(Duration.ofMillis(100)).
                map(i -> "Tick : " + i).
                subscribe(System.out::println);

        if (ttl > 0)
        try {
            Thread.sleep(ttl);
            disposable.dispose();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
