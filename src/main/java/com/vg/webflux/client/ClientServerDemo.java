package com.vg.webflux.client;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.SSLException;
import java.rmi.ServerError;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/18/2019
 */


public class ClientServerDemo {
    private Consumer<SslProvider.SslContextSpec> ctxBuilder;

    @Test
    public void echoTest() {
        HttpServer server = HttpServer.create()
                .port(9000)
                .compress(true)
//                .secure(ctxBuilder)
                .wiretap(true)
                .handle((request, response) -> response.sendString(Mono.just("Hello my world")));
//                .handle((request, response) -> response.sendString(
//                        request.receive().aggregate().asString()
//                ));
        DisposableServer ds =  server.bindNow();

        // client
        String response = HttpClient.create().port(9000)
                .compress(true)
                .wiretap(false) // do not provide detail info
//                .secure()
                .observe((connection, newState) -> {System.out.println("client state:" + newState);})
                .post()
//                .uri("/echo")
                .send(ByteBufFlux.fromString(Flux.just("Hello", " ", "World")))
                .responseContent()
                .aggregate()
                .asString()
                .block();

        assertEquals("Hello World", response);
        ds.dispose();
    }

    @Before
    public void setUp() {
        try {
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            ctxBuilder = ssl -> ssl.sslContext(sslContext);
        } catch (SSLException e) {
            e.printStackTrace();
        }

//        Consumer<SslContextBuilder> builder = b -> b.trustManager(InsecureTrustManagerFactory.INSTANCE);
//        ctxBuilder = ssl -> ssl.sslContext(builder);
//        SslContextBuilder.forClient().sslContextProvider(builder);
        //        ctxBuilder = b -> b.forClient().sslContext(builder);
    }
}
