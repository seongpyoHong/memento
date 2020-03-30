package com.memento.web.dto;

import com.memento.web.domain.History;
import com.memento.web.domain.Url;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@AllArgsConstructor
public class HistoryResponseDto {
    private List<Url> urls = new ArrayList<>();
    private String keyword;

    public HistoryResponseDto(History entity){
        this.urls = entity.getUrls();
        this.keyword = entity.getKeyword();
    }
}
