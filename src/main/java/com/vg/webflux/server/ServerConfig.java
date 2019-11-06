package com.vg.webflux.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vgrigoriev (vladimir.grigoriev@codefactorygroup.com) 11/6/2019
 */

@Configuration
public class ServerConfig {

    @Bean
    public Map<Integer, Feed> getFeeds() {
        Map<Integer, Feed> map = new HashMap();
        for (int i = 0; i<1000;i++) {
            map.put(i, new Feed(i, "feed" + i));
        }
        return map;
    }
}
