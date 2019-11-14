package com.vg.webflux.client.magic;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/14/2019
 */


public interface Observer<T> {
    public void onNext(T integer);

    public void onError(T error);

    public void onComplete();
}