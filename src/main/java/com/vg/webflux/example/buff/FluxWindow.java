package com.vg.webflux.example.buff;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class FluxWindow {
    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofMillis(100))
                .window(5)  // Fixed-size windows (5 elements)
                .flatMap(window -> window.collectList())
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(2);
    }
}
