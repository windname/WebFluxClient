package com.vg.webflux.example;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CompletableFutureTest {
    public static void main(String[] args) {

        CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> "Hello supply async");
        System.out.println(completableFuture.join());
        CompletableFuture completableFuture1 = CompletableFuture.runAsync(() -> System.out.println("Hello runSync"));
        completableFuture = CompletableFuture.completedFuture("Final result");

        // handle future
        CompletableFuture<Void> future = CompletableFuture
                .supplyAsync(() -> "Hello")
                .thenApply(val -> val + " world")
                .thenAccept(System.out::println);

        // handle errors
        CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Boom!");
        }).exceptionally(e -> "fallback").thenAccept(System.out::println);

        CompletableFuture<String> first = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> second = CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<String> combined = first.thenCombine(second, (a, b) -> a + " " + b);
        combined.thenAccept(System.out::println);

        CompletableFuture<Void> all = CompletableFuture.allOf(first, second);
        all.thenRun(() -> System.out.println("All done!"));

        CompletableFuture<String> result = completableFuture.thenApply(s -> "Apply to " + s);
        System.out.println(result.join());
        CompletableFuture result2 = completableFuture.thenAccept(s -> System.out.println("Accept" + s));
        result2.join();
        completableFuture.thenRun(()-> System.out.println("Run on final result"));
        completableFuture.thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));

        String join = CompletableFuture
                .supplyAsync(() -> "task1 ")
                .thenCombine(CompletableFuture.supplyAsync(() -> "task2"), (r1,r2) -> r1 + r2)
                .join();
        System.out.println("join result: " + join);
        CompletableFuture<String> c1 = CompletableFuture.supplyAsync(() -> "Hello2 ");
        CompletableFuture<String> c2 = CompletableFuture.supplyAsync(()-> "World2 ");
        CompletableFuture.allOf(c1, c2).thenAccept(System.out::println);

        // streaming style of completable futures
        String res = Stream.of(c1,c2).map(CompletableFuture::join).collect(Collectors.joining(" "));
        System.out.println("joinining result: " + res);

        CompletableFuture<String> cf0 = CompletableFuture.failedFuture(new RuntimeException("Oops"));
        cf0.handle((r, ex) -> {
            if (ex != null) {
                return ex.getMessage();
            } else {
                return r;
            }
        }).thenAccept(System.out::println);

        Supplier<String> supplier = () -> {
            IntStream.range(1,1000000).forEach(i -> i++); return "All good";
        };
        CompletableFuture<String> f = CompletableFuture.supplyAsync(supplier)
                .orTimeout(1, TimeUnit.MICROSECONDS) // More realistic timeout
                .exceptionally(ex -> "Fallback: Timeout or Error");
        System.out.println(f.join());

        completableFuture = CompletableFuture.supplyAsync(supplier)
            .whenComplete((String input, Throwable exception) -> {
                if (exception != null) {
                    System.err.println("Exception error"); // Exception error
                } else {
                    System.out.println("result: " + input);
                }
            }).exceptionally(throwable ->
            {
                System.out.println("recovering in exceptionally: " + throwable); // recovering in exceptionally: java.util.concurrent.CompletionException
                return "Recovered";
            }).thenAccept(s -> System.out.println("Final result " + s)); // Final result Recovered

        completableFuture.join();

        CompletableFuture.supplyAsync(() -> "When complete").whenComplete((resu, ex) -> {
            if (ex != null) System.err.println("Failed: " + ex);
            else System.out.println("Completed with: " + resu);
        });
    }
}
