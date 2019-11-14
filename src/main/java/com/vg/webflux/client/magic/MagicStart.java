package com.vg.webflux.client.magic;

import java.util.Arrays;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/14/2019
 */


public class MagicStart {
    public static void main(String[] args) {
        // create observeable
        final Observable<Integer> observable = new Observable<>(
                observer -> {

                    // function process numbers
                    Arrays.asList(1,2,3)
                            .stream()
                            .forEach(integer -> observer.onNext(integer));

                    // returns result of function
                    return () -> System.out.println("unsubscribe or stop from array or data source");
                }
        );

        // define observer behaviour and subscribe observable
        Observer observer = new Observer<Integer>() {
            @Override
            public void onNext(Integer integer) {
                System.out.println(integer);
            }

            @Override
            public void onError(Integer error) {

            }

            @Override
            public void onComplete() {

            }
        };

        Subscription subscription = observable.subscribe(observer);

        observable.map(i -> i+5).subscribe(observer);

    }
}
