package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class SchedulersSamples {
    public static void main(String[] args) throws InterruptedException {

        // Runs on the calling thread
        Mono.just("Hello default thread")
                .doOnNext(data -> System.out.println(Thread.currentThread().getName()))
                .subscribeOn(Schedulers.immediate())
                .subscribe(System.out::println);


        // main thread the same as previous as for just it's default
        Mono.just("Hello")
                .map(str -> {
                    System.out.println(Thread.currentThread().getName());
                    return str.toUpperCase();
                })
                .subscribe();

        // All work on one dedicated thread and not main one
        Flux.range(1, 5)
                .subscribeOn(Schedulers.single())
                .subscribe(i -> System.out.println(Thread.currentThread().getName()));

        // delay method Uses `parallel()` scheduler internally
        Mono.delay(Duration.ofMillis(90))
                .subscribe(x -> System.out.println(Thread.currentThread().getName()));

        // No implicit schedulers
        Mono.fromCallable(() -> blockingJdbcQuery())
                .subscribeOn(Schedulers.boundedElastic())  // ✅ Offloads blocking task
                .subscribe(System.out::println);

        // parallel method implies paralle scheduler as well
        Flux.range(1, 10)
                .parallel()                          // Split into parallel rails
                .runOn(Schedulers.parallel())        // Use parallel scheduler
                .map(i -> i * 2)                    // CPU-bound work
                .subscribe(x -> System.out.println("x=" +x + " thread" + Thread.currentThread().getName()));

        Flux.range(0, 4)
                .subscribeOn(Schedulers.boundedElastic())  // Affects everything
                .map(i -> {
                    System.out.println("[map] " + i + " on thread " + Thread.currentThread().getName());
                    return i * 2;
                })                          // Runs on boundedElastic one thread
                .publishOn(Schedulers.parallel())          // Switches to parallel
                .filter(i -> {
                    System.out.println("[filter] " + i + " on thread " + Thread.currentThread().getName());
                    return i > 2;
                })                       // Runs on parallel
                .subscribe();

        Flux.range(1, 5)
                .subscribeOn(Schedulers.boundedElastic())   // blocking upstream
                .map(SchedulersSamples::blockingOp)                    // still on boundedElastic
                .parallel()                                 // split into multiple rails (default = CPU cores)
                .runOn(Schedulers.parallel())               // now each rail runs on a separate thread
                .map(x -> {
                    System.out.println("[new map] " + x + " on thread " + Thread.currentThread().getName());
                    return x+1;
                })  // now really parallel
                .sequential()                               // combine back into one stream/flux not necessary here
                .subscribe(i -> System.out.println(i + " on " + Thread.currentThread().getName()));


        Thread.sleep(1000);

    }

    private static String blockingOp(int i) {
        return blockingJdbcQuery();
    }

    private static String blockingJdbcQuery() {
        try {
            Thread.sleep(100);
            return "bookId:10";
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
