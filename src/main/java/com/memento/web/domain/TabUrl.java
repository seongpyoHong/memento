package com.memento.web.domain;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TabUrl implements Serializable {
    private String address;
    private String keyword;
    private Long visitedTime;
    private Long stayedTime;
    private Integer visitedCount;

    @Builder
    public TabUrl(String keyword,String address, Long visitedTime, Long stayedTime, Integer visitedCount) {
        this.keyword = keyword;
        this.address = address;
        this.visitedTime = visitedTime;
        this.stayedTime = stayedTime;
        this.visitedCount = visitedCount;
    }
}
