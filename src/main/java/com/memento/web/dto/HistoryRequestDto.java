package com.memento.web.dto;


import com.memento.web.common.TransitionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HistoryRequestDto {
    private String url;
    private TransitionType type;
}
