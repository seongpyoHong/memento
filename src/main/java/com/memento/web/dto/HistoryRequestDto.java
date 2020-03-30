package com.memento.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class HistoryRequestDto {
    private String title;
    private Long lastVisitTime;
    private String userUrl;
    private Integer visitCount;
}
