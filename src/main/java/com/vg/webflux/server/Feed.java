package com.vg.webflux.server;

import lombok.*;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */

@Getter
@Setter
@ToString
public class Feed {
    public Feed() {}

    public Feed(int eventId, String name) {
        this.eventId = eventId;
        this.name = name;
    }

    private int eventId;
    private String name;


}
