package com.vg.webflux.client;

import com.vg.webflux.server.Feed;
import com.vg.webflux.server.MissingFeedException;
import org.springframework.http.*;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */


public class SimpleFeedClient {
    public static void main(String[] args) {

        WebClient client = WebClient.create("http://localhost:8080/feed");
        Mono<ClientResponse> result = client.get().uri("/old/{id}", 10000).exchange();
        result.subscribe(r -> System.out.println("Code=" + r.statusCode()));
        result.flatMap(r -> r.bodyToMono(Feed.class)).subscribe(f -> System.out.println("Feed=" + f.toString()));

        // filter non 200 response rezult
        client.get().uri("/{id}", 3).exchange()
                .filter(r -> r.statusCode().is2xxSuccessful())
                .flatMap(r -> r.bodyToMono(Feed.class)).subscribe(System.out::println);

        Mono<Feed> feedMono = client.get()
                .uri("/old/{id}", "2")
                .retrieve()
                .bodyToMono(Feed.class);
        feedMono.subscribe(System.out::println);

//        Flux<Feed> feedFlux = client.get()
//                .uri("/all")
//                .retrieve()
//                .bodyToFlux(Feed.class);
//        feedFlux.subscribe(System.out::println);

        // use inserter to prepare body and put to request MediType
        BodyInserter<Object, ReactiveHttpOutputMessage> inserter
                = BodyInserters.fromObject(new Object());
        WebClient.RequestHeadersSpec<?> request = client.get()
                .uri("/old/{id}", "4")
//                .body(inserter3)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(Charset.forName("UTF-8"))
                .ifNoneMatch("*")
                .ifModifiedSince(ZonedDateTime.now());

        request.retrieve().bodyToMono(Feed.class).subscribe(System.out::println);

        // retrive with error handling
        client
                .get()
                .uri("/old/{id}", 5)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new MissingFeedException("cannot read feed")))
                .bodyToMono(Feed.class)
                .onErrorMap(Throwable::new);

        // bloc thread. Try to avoid it
        String response = request.retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("response=" + response);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("End of program");
    }
}
