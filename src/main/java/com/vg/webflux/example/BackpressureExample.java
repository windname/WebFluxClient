package com.vg.webflux.example;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

public class BackpressureExample {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.range(1, 1000)
                .delayElements(Duration.ofMillis(10));

        flux.subscribe(new DroppingBackpressureSubscriber(10));

        // Sleep to allow asynchronous processing to complete
        try {
            Thread.sleep(1000); // Adjust as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class DroppingBackpressureSubscriber extends BaseSubscriber<Integer> {
    private final int bufferSize;
    private final Queue<Integer> buffer;

    public DroppingBackpressureSubscriber(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new LinkedList<>();
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        // Request an initial batch of items
        request(bufferSize);
    }

    @Override
    protected void hookOnNext(Integer value) {
        System.out.println("Consumed: value");
        request(1);
        if (buffer.size() >= bufferSize) {
            // Drop the oldest item to make space
            IntStream.range(1, bufferSize).forEach(i -> buffer.poll());
            System.out.println("Dropped buffer: ");
        } else {
            // Add the new item to the buffer
            buffer.offer(value);
            System.out.println("Buffered: " + value);
            // Continue processing items
        }

        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void hookOnComplete() {
        System.out.println("Processing complete.");
    }
}
