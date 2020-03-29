package com.memento.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HistroyRequestDto2 {
    private String title;
    private Long lastVisitTime;
    private String userUrl;
    private Integer visitCount;
}   
