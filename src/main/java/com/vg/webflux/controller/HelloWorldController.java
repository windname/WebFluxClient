package com.vg.webflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public Mono<String> hello() {
        // Returning a Mono that emits "Hello, World!" asynchronously
        return Mono.just("Hello, World!");
    }
}