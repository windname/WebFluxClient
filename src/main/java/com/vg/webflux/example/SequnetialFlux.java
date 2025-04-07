package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class SequnetialFlux {
    public static void main(String[] args) throws InterruptedException {
        Flux.range(1,4)
                .subscribeOn(Schedulers.parallel())
                .map(n -> {
                    System.out.println("Map" + n +": " + Thread.currentThread().getName());
                    return n;
                })
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .sequential()
                .subscribe(n ->System.out.println("Sub" + n +": " + Thread.currentThread().getName()));

        Thread.sleep(1000);
    }
}
