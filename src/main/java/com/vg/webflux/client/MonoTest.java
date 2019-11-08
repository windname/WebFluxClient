package com.vg.webflux.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */


public class MonoTest {

    public static void main(String[] args) {
        Mono<String> helloWorld = Mono.just("Hello Just !");
        helloWorld.subscribe(System.out::println);

        // Creating an empty Mono
        Mono<Object> emptyMono = Mono.empty();
        emptyMono.subscribe(System.out::println);

        // Creating a mono from a Callable
        Mono<String> helloWorldCallable = Mono.fromCallable(() -> "Hello Callable !");
        helloWorldCallable.subscribe(System.out::println);

        // Creating a mono from a Future
        CompletableFuture<String> helloWorldFuture = CompletableFuture.supplyAsync(() ->  "Hello CompletableFuture");
        Mono<String> monoFromFuture = Mono.fromFuture(helloWorldFuture);
        monoFromFuture.subscribe(System.out::println);

        // Creating a mono from a supplier
        Random rnd = new Random();
        Mono<Double> monoFromSupplier = Mono.fromSupplier(new Random()::nextDouble);
        monoFromSupplier.subscribe(System.out::println);

        // You can also create a mono from another one.
        Mono<Double> monoCopy = Mono.from(monoFromSupplier);

        Mono<Double> deferMono = Mono.defer(() -> monoFromSupplier);

        Mono<Integer> monoFromFlux = Mono.from(Flux.range(1, 10));
        monoFromFlux.subscribe( i -> System.out.println("Mono from flux " + i));

        // create error and conusme it
        Mono.error(() -> new RuntimeException("hello error")).subscribe(
                successValue -> System.out.println(successValue),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mono consumed.")
        );

    }
}
