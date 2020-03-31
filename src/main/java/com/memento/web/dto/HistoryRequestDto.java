package com.memento.web.dto;

import lombok.*;

@ToString
@Getter
@Setter
public class HistoryRequestDto {
    private String title;
    private Long lastVisitTime;
    private String userUrl;
    private Integer visitCount;

    @Builder
    public HistoryRequestDto(String title, Long lastVisitTime, String userUrl, Integer visitCount) {
        this.title = title;
        this.lastVisitTime = lastVisitTime;
        this.userUrl = userUrl;
        this.visitCount = visitCount;
    }
}
