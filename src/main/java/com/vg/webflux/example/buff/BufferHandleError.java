package com.vg.webflux.example.buff;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BufferHandleError {
    public static void main(String[] args) throws InterruptedException {
        // silent handle out of buffer error
        Flux.interval(Duration.ofMillis(10))
                .take(100)
                .onBackpressureDrop(dropped ->       // Drops items silently
                         System.out.println("Dropped silent: " + dropped)
                )
                .delayElements(Duration.ofMillis(100)) // Slow consumer (100ms)
                .subscribe(System.out::println);

        // alternative implementation
        Flux.interval(Duration.ofMillis(10))       // Fast producer (10ms)
                .take(100) // limit the number of elemnts
                .onBackpressureBuffer(2,
                        dropped -> System.out.println("Dropped oldest: " + dropped),
                        BufferOverflowStrategy.DROP_OLDEST  // Silently drop oldest items
                )
                .delayElements(Duration.ofMillis(100)) // Slow consumer (100ms)
                .subscribe(System.out::println);

        // collect drop elements
        Flux.interval(Duration.ofMillis(10))
                .onBackpressureDrop()
                .take(20)
                .collect(
                        ArrayList::new,
                        ArrayList::add
                )
                .subscribe(list ->
                        System.out.println("Collected " + list.size() + " items")
                );

        Flux.interval(Duration.ofMillis(10)) // produce everu 10 ms
                .take(100)// limit the number of produced elements
                .onBackpressureBuffer(3, BufferOverflowStrategy.DROP_OLDEST)  // Tiny buffer and drop silent
                .delayElements(Duration.ofMillis(100))  // Slow consumer
                .collectList()
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(10);
    }
}
