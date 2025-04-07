package com.vg.webflux.example.lessons;

import reactor.core.publisher.Mono;

public class FlatMapOperations {
    public static void main(String[] args) {

        Mono.just("John")
                .flatMap(s -> findPerson(s))
                .subscribe(System.out::println);
    }

    public record Person(String name, int age) {}
    public static Mono<Person> findPerson(String name) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Mono.just(new Person(name, 10));
    }
}
