package com.vg.webflux.server;

import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/26/2019
 */


public class FunctionalSimpleServer {
    public static void main(String[] args) {

        RouterFunction<ServerResponse> route = route()
                .GET("/index",  request -> ok().body(fromObject("index page")))
                .GET("/feed",  request -> ok().body(fromObject("feed page")))
                .build();

        HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

        HttpServer server = HttpServer.create()
                .port(9000)
                .compress(true)
                .wiretap(true)
                .handle(adapter); // routing
//                .handle((request, response) -> response.sendString(Mono.just("Hello my world!!!")));

        DisposableServer ds =  server.bindNow();
        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            ds.dispose();
        }

    }
}
