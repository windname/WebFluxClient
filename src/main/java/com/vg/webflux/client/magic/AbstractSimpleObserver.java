package com.vg.webflux.client.magic;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/14/2019
 */


public abstract class AbstractSimpleObserver<T> implements Observer<T> {

    @Override
    public void onError(Object error) {

    }

    @Override
    public void onComplete() {

    }
}
