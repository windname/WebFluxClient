package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class DifferentThreads {
    public static void main(String[] args) throws InterruptedException {

        Flux.range(1, 4)
                .flatMap(n -> Mono.just(n)
                        .doOnNext(x-> System.out.println("Mono:" + Thread.currentThread().getName())))
                .subscribe(
                        x ->  System.out.println("Subs: " + Thread.currentThread().getName())
                );

        System.out.println("Next example");
        Flux.range(1, 4)
                .map(n -> {System.out.println("Map" + n + ": " + Thread.currentThread().getName());
                    return n;})
                .flatMap(n -> {
                    switch (n) {
                        case 1: return Mono.just(n).subscribeOn(Schedulers.immediate())
                                .doOnNext(x-> System.out.println("Mono" + n + ": "  + Thread.currentThread().getName()));
                        case 2: return Mono.just(n).subscribeOn(Schedulers.single())
                                .doOnNext(x-> System.out.println("Mono" + n + ": " + Thread.currentThread().getName()));
                        case 3: return Mono.just(n).subscribeOn(Schedulers.boundedElastic())
                                .doOnNext(x-> System.out.println("Mono" + n + ": " + Thread.currentThread().getName()));
                        case 4: return Mono.just(n).subscribeOn(Schedulers.parallel())
                                .doOnNext(x-> System.out.println("Mono" + n + ": " + Thread.currentThread().getName()));
                        default: return Mono.empty();
                    }
                })
                .subscribeOn(Schedulers.immediate())
                .subscribe(
                        n ->  System.out.println("Subs" + n + ": " + Thread.currentThread().getName())
                );

        Thread.sleep(1000);
    }
}
