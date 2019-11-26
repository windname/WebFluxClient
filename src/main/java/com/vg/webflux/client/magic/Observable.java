package com.vg.webflux.client.magic;

import java.util.function.Function;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/14/2019
 */


public class Observable<T> {
    private Function<Observer<T>,Subscription> subscribe;

    public Observable(Function<Observer<T> , Subscription> subscribeParam) {
        this.subscribe = subscribeParam;
    }

    public Subscription subscribe(Observer<T> observer) {
        return this.subscribe.apply(observer);
    }

    // oprator map
    public <U> Observable<U> map( Function<T, U> projection) {

        return
                new Observable<>( observer -> {
                    this.subscribe(new AbstractSimpleObserver<T>() {

                        @Override
                        public void onNext(T data) {
                            observer.onNext(projection.apply(data));
                        }
                    });

                    return () -> System.out.println("dispose");
                });
    }


}