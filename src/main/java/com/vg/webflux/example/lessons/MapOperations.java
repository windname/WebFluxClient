package com.vg.webflux.example.lessons;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.HashMap;

public class MapOperations {
    public static void main(String[] args) {
        Flux.just("one", "two", "three")
                .map(s -> s.toUpperCase())
                .subscribe(System.out::println);

        Flux.just("one", "two", "three")
                .map(s -> s.length())
                .subscribe(System.out::println);

        Flux.just("one", "two", "three")
                .map(s -> Tuples.of(s, s.length()))
                .subscribe(System.out::println);

        Flux.just("one", "two", "three")
                .reduce(new HashMap<String, Integer>(), (m, s) -> {
                    m.put(s, s.length());
                    return m;})
                .subscribe(System.out::println);

        Flux.just(2,3,4)
                .map(n -> n*2)
                .subscribe(System.out::println);

        Mono.just("string")
                .flatMap(s -> Mono.just(s.length()))
                .subscribe(System.out::println);

    }
}
