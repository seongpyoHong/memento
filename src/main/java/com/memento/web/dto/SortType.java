package com.memento.web.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortType {
    DEFAULT("default"), VISITCOUNT("visitCount"), RECENT("recent"), STAYING("staying");
    private final String name;
    public String getName() {
        return name;
    }
}