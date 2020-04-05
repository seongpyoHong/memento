package com.memento.web.dto;

import lombok.*;

@ToString
@Getter
@Setter
public class HistoryRequestDto {

    private Integer tabId;
    private String title;
    private String url;
    private Long visitedTime;
    private Long stayedTime;

    @Builder
    public HistoryRequestDto(Integer tabId, String title, String url, Long visitedTime, Long stayedTime) {
        this.tabId = tabId;
        this.title = title;
        this.url = url;
        this.visitedTime = visitedTime;
        this.stayedTime = stayedTime;
    }
}
