package com.vg.webflux.client.skycity;

import lombok.Getter;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;

import java.util.function.Consumer;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/20/2019
 */

@Getter
public class NettyFeedServer {
    public DisposableServer disposableServer;
    IndexEventHandler eventHandler = new IndexEventHandler();

    public NettyFeedServer() {

        Mono<ServerResponse> test = null;

        Consumer<? super HttpServerRoutes> routesBuilder  = routes -> routes.get("/event/{id}",
//                (req, res) -> res.sendString(Mono.just(req.param("id"))));
            (req, res) -> res.status(200).sendString(Mono.just(req.param("id"))));
//                 (req, res) -> res.sendString(Mono.delay(Duration.ofSeconds(3)).thenReturn("OK")));
//                (req, res) -> res.send(req.receive().retain()));
//                 (req, res) -> res.send(ByteBufFlux.fromString(Mono.just("hello"))));
//                (req, res) -> res.sendString(ByteBufFlux.fromStream(Arrays.asList("one","two").stream())));
//                (req, res) -> res.sendString(Mono.just("Hello world")));
//                (req, res) -> res.send(ByteBufFlux.fromStream(Arrays.asList("one","two").stream()))));


        // !!!NOte Unfortunatelly netty doesn't allow to send a DTO direct without transformations
        HttpServer server = HttpServer.create().port(9000);
        server.route(routesBuilder);
        disposableServer = server.bindNow();


    }

    public DisposableServer bindServer(){
        return disposableServer;
    }
}
