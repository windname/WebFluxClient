package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class FluxCreate {
    public static void main(String[] args) {

        Mono<String> monoString = Mono.just("Hello, Mono!");
        monoString.subscribe(System.out::println);  // Prints: Hello, Mono!

        Mono<Void> emptyMono = Mono.empty();
        emptyMono.subscribe(
                data -> {},
                error -> {},
                () -> System.out.println("No value emitted")
        );

        // handle blocking code reactively.
        Mono<Integer> result = Mono.fromCallable(() -> {
            // Some blocking operation (e.g., reading a file or DB query)
            return 10;
        });
        result.subscribe(System.out::println);

        Mono<String> errorMono = Mono.error(new RuntimeException("An error occurred"));
        errorMono.subscribe(
                item -> {},
                error -> System.out.println("Error: " + error.getMessage())  // Prints: Error: An error occurred
        );

        // lazily execute the logic
        Mono<String> deferredMono = Mono.defer(() -> Mono.just("Deferred Value"));
        deferredMono.subscribe(System.out::println);  // Prints: Deferred Value

        System.out.println("just");
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);
        flux.subscribe(System.out::print);  // Prints: 1, 2, 3, 4, 5

        System.out.println("\nrange");
        Flux<Integer> rangeFlux = Flux.range(1, 5);  // Generates numbers from 1 to 5
        rangeFlux.subscribe(System.out::print);  // Prints: 1, 2, 3, 4, 5

        System.out.println("\nfromIterable");
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        Flux<String> namesFlux = Flux.fromIterable(names);
        namesFlux.subscribe(System.out::print);

        System.out.println("\ngenerate");
        Flux<Integer> generatedFlux = Flux.generate(sink -> {
            int value = (int) (Math.random() * 100);
            sink.next(value);
            if (value > 90) {
                sink.complete();  // End the stream when value > 90
            }
        });
        generatedFlux.subscribe(System.out::print);

        System.out.println("\ncreate");
        Flux<Integer> createdFlux = Flux.create(sink -> {
            sink.next(1);
            sink.next(2);
            sink.complete();  // End the stream
        });
        createdFlux.subscribe(System.out::print);

        Flux<String> errorFlux = Flux.error(new RuntimeException("Something went wrong"));
        errorFlux.subscribe(
                item -> {},
                error -> System.out.println("Error: " + error.getMessage())  // Prints: Error: Something went wrong
                );

        Mono<String> mono = Mono.just("Hello");
        Flux<String> fluxConcat = Flux.just("World", "Reactor");

        mono.concatWith(fluxConcat).subscribe(System.out::println);

        Mono<String> mono1 = Mono.just("Hello");
        Mono<String> mono2 = Mono.just("World");

        Mono.zip(mono1, mono2).subscribe(tuple ->
                System.out.println(tuple.getT1() + " " + tuple.getT2())  // Prints: Hello World
        );
    }
}
