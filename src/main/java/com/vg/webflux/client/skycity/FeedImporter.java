package com.vg.webflux.client.skycity;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/25/2019
 */


public class FeedImporter {
    public static void main(String[] args) {
        SpringFeedServer server = new SpringFeedServer(9000, "localhost");

        WebClient client = WebClient.create("http://localhost:9000/");

        FeedImporter clientFeed = new FeedImporter();
        clientFeed.separateRequests(client);


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            server.getServer().dispose();
            System.out.print("End program");
        }

    }

    public FeedImporter() {
    }

    public void separateRequests(WebClient client) {
        client.get().uri("/index", 2)
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(index -> System.out.println("events are " + index.keySet()));

        client.get().uri("/feed/{id}", 2)
                .retrieve()
                .bodyToMono(Integer.class)
                .subscribe(feed -> System.out.println("feed is " + feed));
    }

    public void sequentialRequest(WebClient client) {

    }
}
