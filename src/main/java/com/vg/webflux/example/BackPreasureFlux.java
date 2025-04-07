package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class BackPreasureFlux {
    public static void main(String[] args) throws InterruptedException {

        // 1..256, Exception
//        int bufferSize = 10;

        // 1..512, Exception. processed by 2 threads parallel-2, and 4
        int bufferSize = 500;

        Flux.range(1, 1000)
                .onBackpressureBuffer(bufferSize)
                .flatMap(item ->
                        Mono.delay(Duration.ofMillis(100))
                                .thenReturn(item)
                )
                .doOnNext(x -> System.out.println(Thread.currentThread().getName()))
                .subscribe(
                        System.out::println,
                        err -> System.err.println("Error: " + err),
                        () -> System.out.println("Done")
                );

        Thread.sleep(1000);
    }
}
