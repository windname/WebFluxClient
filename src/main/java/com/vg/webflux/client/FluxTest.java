package com.vg.webflux.client;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */


public class FluxTest {
    public static void main(String[] args) {
        Flux<String> flux = Flux.just("red", "white", "blue","green");

        Flux<String> upper = flux
//                .log()
                .map(String::toUpperCase);
        upper.subscribe(System.out::println);

        // log method helps to output elements before program stop
      flux.log().parallel()
            .runOn(Schedulers.parallel())
            .subscribe(i -> System.out.println(i));
    }
}
