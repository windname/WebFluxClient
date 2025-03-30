package com.vg.webflux.example;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */


public class FluxTest {
    public static void main(String[] args) {
        Flux<String> flux = Flux.just("red", "white", "blue", "green");

        Flux<String> upper = flux
                .map(String::toUpperCase);
        upper.subscribe(System.out::println);

        // create three lists with numbers from 1 to 10
        Flux.range(1, 10)
                .parallel(3)
                .collect(ArrayList::new, List::add)
                .subscribe(list -> System.out.print("List=" + list));

        // infinite flux every 100 ms new number
        Flux.interval(Duration.ofMillis(10)).
                map(i -> "Tick : " + i)
                .subscribe(System.out::println);

        // log method helps to output elements before program stop
        Disposable disposable = flux.log().parallel()
                .runOn(Schedulers.parallel())
                .subscribe(i -> System.out.println(i));

        System.out.println("Before dispose");
        try {
            Thread.sleep(1000);
            // stop infinite flux explicitly
            disposable.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // sequential execution of flux
        Flux.range(1, 3).
                flatMap(n -> {
                    System.out.println("In flatMap n=" + n + " --- Thread is : " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(100);
                        System.out.println("After Thread.sleep n=" + n);
                        return Mono.just(n);
                    } catch (InterruptedException e) {
                        return Mono.error(e);
                    }
                })
                .map(n -> {
                    System.out.println("In map n=" + n);
                    return n;
                })
                .subscribe(System.out::println);
    }
}
