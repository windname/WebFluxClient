package com.vg.webflux.client;

import com.vg.webflux.server.Feed;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */


public class SimpleFeedClient {
    public static void main(String[] args) {



        WebClient client = WebClient.create("http://localhost:8080/feed");
        Mono<ClientResponse> result = client.get().uri("/{id}", 1).exchange();
        result.subscribe(r -> System.out.println("Code=" + r.statusCode()));

        result.flatMap(r -> r.bodyToMono(Feed.class)).subscribe(f -> f.toString());


        Mono<Feed> feedMono = client.get()
                .uri("/old/{id}", "2")
                .retrieve()
                .bodyToMono(Feed.class);
        feedMono.subscribe(System.out::println);

        Flux<Feed> feedFlux = client.get()
                .uri("/all")
                .retrieve()
                .bodyToFlux(Feed.class);

        feedFlux.subscribe(System.out::println);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("End of program");
    }
}
