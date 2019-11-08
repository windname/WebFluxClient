package com.vg.webflux.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */


public class FluxTransform {
    public static void main(String[] args) {
        Flux<Integer> squared = Flux.range(1, 10)
                .map(x -> x * x);
        squared.subscribe(x -> System.out.print(x + ", "));
        System.out.println();

        // flux<Integert> to Mono<List|<Integer>>
        Flux.range(1, 20).filter(i -> i%2 == 0)
                .collectList().subscribe(list -> System.out.print("list=" + list));
        System.out.println();

        Mono<String> monos = Mono.just("FlatMap");
        monos.flatMap(m -> Mono.just(m.length())).subscribe(System.out::println);

        Mono<Mono<String>> monos2 = Mono.just(Mono.just("FlatMap"));
        Mono<String> reduceMono = monos2.flatMap(m -> m);

        // zip monos
        Mono<String> mono1 = Mono.justOrEmpty("Hello");
        Mono<String> mono2 = Mono.justOrEmpty("world");
        mono1.zipWith(mono2).map(tuple -> tuple.getT1() + " zip " + tuple.getT2()).subscribe(System.out::println);
        mono1.zipWith(mono2).map(tuple -> {
            String str = tuple.getT1() + " return " + tuple.getT2();
            return str;
        }).subscribe(System.out::println);


        Flux.range(1, 10).take(5).subscribe(System.out::print);


    }
}
