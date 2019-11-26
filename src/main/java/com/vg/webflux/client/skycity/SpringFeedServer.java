package com.vg.webflux.client.skycity;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/25/2019
 */

import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class SpringFeedServer {

    DisposableServer disposableServer = null;

    public SpringFeedServer(int port, String host) {

        IndexEventHandler handler = new IndexEventHandler();
        RouterFunction<ServerResponse> route = route()
                .GET("/index", handler::getIndex)
                .GET("/feed/{id}", handler::getFeed)
                .build();

        HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

        HttpServer server = HttpServer.create()
                .port(port)
                .compress(true)
                .wiretap(true)
                .handle(adapter);

        disposableServer = server.bindNow();
    }

    public DisposableServer getServer() {
        return  disposableServer;
    }

    public static void main(String[] args) {
        SpringFeedServer server = new SpringFeedServer(9000, "localhost");

        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.disposableServer.dispose();
    }

}
