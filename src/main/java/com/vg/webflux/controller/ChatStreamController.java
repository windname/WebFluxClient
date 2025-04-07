package com.vg.webflux.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/chat")
public class ChatStreamController {

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponse(@RequestParam(defaultValue = "Hello from WebFlux") String prompt) {
        String[] tokens = simulateGPT(prompt);
        return Flux.fromArray(tokens)
                .delayElements(Duration.ofMillis(300)); // simulate real-time stream
    }

    private String[] simulateGPT(String prompt) {
        return (prompt + " ...this is a reactive stream.").split(" ");
    }
}
