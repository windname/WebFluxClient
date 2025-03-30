package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */

public class Scheduler {
    public static void main(String[] args) {

        // everything in parallel scheduler sequentially
        Flux.range(1, 3)
                .map(n -> threadLog(n, "map1"))
                .flatMap(n -> Mono.just(n).map(nn -> threadLog(nn, "mono")))
                .subscribeOn(Schedulers.parallel())
                .subscribe(n -> {
                    threadLog(n, "subs");
                    System.out.println(n);
                });

        separateSample();

        // everything in main scheduler sequentially
        Flux.range(1, 3)
                .parallel()
                .map(n -> threadLog(n, "map1"))
                .flatMap(n -> Mono.just(n).map(nn -> threadLog(nn, "mono")))
                .subscribe(n -> {
                    threadLog(n, "sub");
                    System.out.println(n);
                });

        separateSample();

        // everything in parallel scheduler and multiple threads
        Flux.range(1, 3)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(n -> threadLog(n, "map1"))
                .flatMap(n -> Mono.just(n).map(nn -> threadLog(nn, "mono")))
                .subscribe(n -> {
                    threadLog(n, "sub");
                    System.out.println(n);
                });

        separateSample();

        // map1 - parallel scheduler and other mono, subs - elastic
        Flux.range(1, 3)
                .map(n -> threadLog(n, "map1"))
                .flatMap(n -> Mono.just(n).map(nn -> threadLog(nn, "mono")).subscribeOn(Schedulers.boundedElastic()))
                .subscribeOn(Schedulers.parallel())
                .subscribe(n -> {
                    threadLog(n, "subs");
                    System.out.println(n);
                });

        separateSample();

        // map1. mono - main, map2, subs - single
        Flux.range(1, 3)
                .map(n -> threadLog(n, "map1"))
                .flatMap(n -> Mono.just(n).map(nn -> threadLog(nn, "mono")))
                .publishOn(Schedulers.single())
                .map(n -> threadLog(n, "map2"))
                .subscribe(n -> {
                    threadLog(n, "subs");
                    System.out.println(n);
                });

        separateSample();

    }

    private static void separateSample() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    private static <T> T threadLog(T el, String operation) {
        System.out.println(operation + " -- " + el + " -- " +
                Thread.currentThread().getName());
        return el;
    }

}
