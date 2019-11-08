package com.vg.webflux.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private Map<Integer, Feed> feeds;

    @GetMapping("/{id}")
    private Mono<Feed> getFeedById(@PathVariable Integer id) {
        return Mono.justOrEmpty(feeds.get(id));
    }

    @GetMapping("/old/{id}")
    private Feed getOldFeedById(@PathVariable Integer id) {
        Optional<Feed> feedOpt = Optional.ofNullable(feeds.get(id));
        feedOpt.orElseThrow(() -> new MissingFeedException("cannot find feed"));
        return feedOpt.get();
    }

    @GetMapping("/all")
    private Flux<Feed> getAllFeeds() {
        return Flux.fromIterable(feeds.values());
    }
}
