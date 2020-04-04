package com.memento.web.dto;

import com.memento.web.domain.History;
import com.memento.web.domain.Url;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@AllArgsConstructor
public class HistoryResponseDto implements Serializable{
    private String id;
    private List<Url> urls = new ArrayList<>();
    private String keyword;

    public HistoryResponseDto(History entity){
        this.id = entity.getId();
        this.urls = entity.getUrls();
        this.keyword = entity.getKeyword();
    }

    public void sortByVisitedCount() {
        this.urls.sort((url, t1) -> t1.getVisitedCount().compareTo(url.getVisitedCount()));
    }
    public void sortByVisitedtime() {
        this.urls.sort((url, t1) -> t1.getVisitedTime().compareTo(url.getVisitedTime()));
    }
    public void sortByStayedtime() {
        this.urls.sort((url, t1) -> t1.getStayedTime().compareTo(url.getStayedTime()));
    }
}
