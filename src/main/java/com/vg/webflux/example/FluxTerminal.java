package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FluxTerminal {
    public static void main(String[] args) {
        Flux<String> flux = Flux.just("red", "white", "blue", "green");

        flux.subscribe(
                item -> System.out.print(item + " ")
        );
        System.out.println();

        flux.subscribe(
                item -> System.out.print(item + " "),  // Consumer for each emitted item
                error -> System.err.println("Error: " + error),  // Error handler
                () -> System.out.println("Completed!")  // Completion handler
        );

        String result = flux.blockFirst();  // Blocks and returns the first item emitted.
        System.out.println(result);

        Mono<List<String>> collection = flux.collectList();  // Collect all items into a list
        collection.subscribe(list -> System.out.println("Collected items: " + list));

        //convert flux to stream
        flux.toStream().forEach(System.out::print);
        System.out.println();

        long count = flux.count().block();
        System.out.println("Number of elements: " + count);

        // reduce function that returm Mono<Long>
        Flux.range(1,5)
            .reduce((a, b) -> a + b).subscribe(sum -> System.out.println("Reduced: " + sum));

        // strange first behaviour
        flux.first().subscribe(item -> System.out.print("First item: " + item),
                error -> System.err.println("Error: " + error),  // Error handler
                () -> System.out.println(""));

        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5).log("before-first");
        System.out.println("Numbers: " + numbers.collectList().block());
        numbers.log("original")
                .first()
                .defaultIfEmpty(-1)
                .subscribe(item -> System.out.println("First item: " + item));

        flux.take(3).subscribe(System.out::print);  // Take the first 3 items from Flux
        System.out.println();

        Flux.just("Quick data")
                .delayElements(Duration.ofMillis(1))  // Emits after 2 second
                .timeout(Duration.ofMillis(2))       // Timeout after 1 seconds
                .subscribe(
                        data -> System.out.println("Received: " + data),
                        error -> System.out.println("Error: " + error.getMessage()), // error expected
                        () -> System.out.println("Completed successfully")
                );

        try {
            detailedDelayRequest(1,2);
            detailedDelayRequest(2,1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // dedicated onError
        Flux.error(new RuntimeException("Something went wrong"))
                .onErrorReturn("Default Value")
                .subscribe(System.out::println);

        // dedicated onError
        Flux.error(new RuntimeException("Something went wrong"))
                                .subscribe(
                                        data -> System.out.println("data " + data),
                                        error -> System.out.println("Error data" + error),
                                        () -> System.out.println("completed")
                                );

        Flux.range(1, 10)
                .map(i -> expensiveOperation(i))
                .onTerminateDetach()
                .subscribe(System.out::print);
    }

    private static Object expensiveOperation(Integer i) {
        return i.toString();
    }

    private static void detailedDelayRequest(int delay, int timeout) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        System.out.println("Main thread: " + Thread.currentThread().getName());

        Flux.just("Quick data")
                .delayElements(Duration.ofMillis(1))  // Runs on parallel-1 thread
                .timeout(Duration.ofMillis(2))
                .doOnSubscribe(sub -> System.out.println("Subscribed on: " + Thread.currentThread().getName()))
                .subscribe(
                        data -> {
                            System.out.println("Received on: " + Thread.currentThread().getName());
                            System.out.println("Data: " + data);
                        },
                        error -> {
                            System.out.println("Error on: " + Thread.currentThread().getName());
                            System.out.println("Error: " + error.getMessage());
                            latch.countDown();
                        },
                        () -> {
                            System.out.println("Completed on: " + Thread.currentThread().getName());
                            latch.countDown();
                        }
                );

        latch.await();  // Block main thread until Flux completes
    }
}
