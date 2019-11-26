package com.vg.webflux.client.skycity;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/20/2019
 */


public class IndexEventHandler {
    Map<Integer, Integer> originMap = new HashMap() {{
        put(2,2);
        put(3,1);
        put(4,1000);
    }};

    public Mono<ServerResponse> getIndex(ServerRequest request) {

        return Mono.just(originMap)
                .flatMap(index -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(index)));
    }

    public Mono<ServerResponse> getFeed(ServerRequest request) {
        Mono<ServerResponse> responseMono = Mono.just(originMap.getOrDefault(Integer.parseInt(request.pathVariable("id")), 0))
                .flatMap(feed -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(feed)));
        return responseMono;
    }
}
