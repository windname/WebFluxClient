package com.vg.webflux.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class FluxNonTerminal {
    public static void main(String[] args) {

        // read data from a resource, for example file and transform into flux
        Flux.using(
                () -> "MyResource",                          // 1. Create resource
                resource -> Flux.just(resource, " Decorate "),     // 2. Use resource to make Flux
                resource -> System.out.println("Result: " + resource)  // 3. Clean up and close resource
        ).subscribe(System.out::println);

        //  Transforms each emitted item into another item
        Flux<Integer> numbers = Flux.just(1, 2, 3);
        Flux<String> stringsMap = numbers.map(i -> "Number: " + i);
        stringsMap.subscribe(System.out::println);

        // Transforms each emitted item into a Publisher
        Flux<String> stringsFlatMap = numbers.flatMap(i -> Flux.just("Number: " + i, "Squared: " + i * i));
        stringsFlatMap.subscribe(System.out::println);  // Prints multiple values: Number: 1, Squared: 1, ...

        System.out.println("Filter");
        Flux<Integer> evenNumbers = numbers.filter(i -> i % 2 == 0);
        evenNumbers.subscribe(System.out::print);  // Prints: 2, 4

        System.out.print("\nDistinct");
        numbers = Flux.just(1, 2, 2, 3, 3, 3);
        numbers.distinct().subscribe(System.out::print);

        // keeps order
        System.out.print("\nConcat: ");
        Flux<Integer> first = Flux.just(2, 1, 3, 4);
        Flux<Integer> second = Flux.just(4, 5, 6);
        first.concatWith(second).subscribe(System.out::print);

        System.out.print("\nResume after error: ");
        Flux.just(1, 2, 3).map(i -> {
                    if (i == 2) throw new RuntimeException("Error!");
                    return i;
                }).onErrorResume(e -> Flux.just(10, 20))  // Fallback on error
                .subscribe(System.out::print); // 1, 10, 20

        Mono<String> school = Mono.just("Davinci");
        // Simulating fetching items for each user (flatMapMany will flatten them)
        school.flatMapMany(userId -> getItemsForUser(userId))
                .subscribe(item -> System.out.print("Received: " + item + " "));

        Flux<String> schools = Flux.just("Davinci", "Newton");
        schools.doOnNext(s-> System.out.println("\n" + s))
                .flatMap(s -> getItemsForUser(s)).subscribe(System.out::print);

        schools
                .parallel()  // Split the flux into parallel rails
                .runOn(Schedulers.parallel())  // Specify scheduler
                .doOnNext(s -> System.out.println("\nProcessing: " + s))
                .flatMap(s -> getItemsForUser(s))
                .sequential()  // Merge back to single flux
                .subscribe(System.out::println);

        try {
            Thread.sleep(100);
            blockingMerge();
            blockingRetry();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void blockingRetry() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        System.out.print("\nRetry on error: ");
        Flux.just(1, 2, 3)
                .map(i -> {
                    if (i == 2) throw new RuntimeException("Error!");
                    return i;
                }).retry(3)// Retry 3 times
                .doOnTerminate(() -> latch.countDown())
                .subscribe(
                        i -> System.out.print(i + ", "),
                        error -> System.out.println(error.getMessage()),
                        () -> {}
                );  // Retries on error 1,1,1,1
        latch.await();
    }

    private static void blockingMerge() throws InterruptedException {
        Flux<Integer> first;
        Flux<Integer> second;
        CountDownLatch latch = new CountDownLatch(1);
        // parallel adding of elements
        System.out.print("\nMerge");
        first = Flux.just(2, 1, 3, 4).delayElements(Duration.ofMillis(2));
        second = Flux.just(4, 5, 6).delayElements(Duration.ofMillis(1));
        first.mergeWith(second)
                .doOnComplete(() -> latch.countDown())
                .subscribe(System.out::print);
        latch.await();
    }

    // Simulate fetching items for a user (could be a database query, API call, etc.)
    private static Flux<String> getItemsForUser(String school) {
        switch (school) {
            case "Davinci":
                return Flux.just("Item1-A", "Item1-B", "Item1-C");
            case "Newton":
                return Flux.just("Item2-A", "Item2-B");
            default:
                return Flux.error(new IllegalArgumentException("Unknown school"));
        }
    }
}
