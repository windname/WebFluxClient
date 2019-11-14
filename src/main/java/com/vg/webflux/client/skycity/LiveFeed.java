package com.vg.webflux.client.skycity;

import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/13/2019
 */

@Log
public class LiveFeed {

    Map<Integer, Integer> originMap = new HashMap() {{
        put(2,2);
        put(3,1);
    }};

    public static void main(String[] args) {

        LiveFeed liveFeed = new LiveFeed();

//        liveFeed.processVersion1();
        liveFeed.processVersion2();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void processVersion1() {
        getIndex()
                .parallel()
                .filter(event -> eventUpdated(event))
                .flatMap(event -> readEvent(event))
                .runOn(Schedulers.parallel())
                .subscribe();
    }

    public void processVersion2() {
        getIndex()
                .parallel()
                .filter(event -> eventUpdated(event))
                .doOnNext(event -> {
                    System.out.print("Start read event " + event.eventId);
                    readEvent(event);
                    System.out.print("End read event " + event.eventId);
                })
                .runOn(Schedulers.parallel())
                .subscribe();
    }

    public boolean eventUpdated(Event event) {
        if (!originMap.containsKey(event.eventId)) {
            originMap.put(event.eventId, event.version);
            return true;
        }
        else if (originMap.get(event.eventId).equals(event.version)) {
            return false;
        } else {
            originMap.put(event.eventId, event.version);
        }
        return true;
    }

    public Flux<Event> getIndex() {
        Index index = new Index();
        index.events = Arrays.asList(new Event(1,1), new Event(2,2), new Event(3,3));
        return Flux.fromIterable(index.events);
    }

    public Mono readEvent(Event event) {
        log.info("Read event " + event.eventId);
        return Mono.empty();
    }

    class Index {
        List<Event> events;
    }

    class Event {
        public Event(int eventId, int version) {
            this.eventId = eventId;
            this.version = version;
        }
        int eventId;
        int version;
    }
}
