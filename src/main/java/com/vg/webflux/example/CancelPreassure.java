package com.vg.webflux.example;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class CancelPreassure {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> cancel = Flux.range(1, 10);

        cancel.subscribe(new BaseSubscriber<Integer>() {

            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println(value);
                if (value % 3 ==0)
                cancel();
            }
        });

        Thread.sleep(1000);
    }
}
