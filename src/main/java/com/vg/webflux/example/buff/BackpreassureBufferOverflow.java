package com.vg.webflux.example.buff;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class BackpreassureBufferOverflow {
    public static void main(String[] args) throws InterruptedException {

        // generate error
        Flux.interval(Duration.ofMillis(10))  // Fast producer (every 10ms)
                .onBackpressureBuffer(2,         // Buffer only 2 items
                        dropped -> System.out.println("Dropped: " + dropped)  // Overflow handler
                )
                .delayElements(Duration.ofMillis(100))  // Slow consumer (100ms/item)
                .subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(1);
    }
}
