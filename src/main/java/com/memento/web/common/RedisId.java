package com.memento.web.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RedisId {
    private String id;
    @Builder
    public RedisId(String userName, Integer tabId) {
        this.id = userName + "+" + String.valueOf(tabId);
    }
}
