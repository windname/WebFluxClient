package com.vg.webflux.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MissingFeedException extends RuntimeException{
    public MissingFeedException(String message) {
        super(message);
    }
}
