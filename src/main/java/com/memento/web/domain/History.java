package com.memento.web.domain;

import com.memento.web.dto.HistoryRequestDto;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.Id;
import java.util.*;

@ToString
@NoArgsConstructor
@Getter
@Document(collection = "history")
public class History {
    @Id
    private String id;
    private String keyword;
    private List<Url> urls = new ArrayList<>();

    @Builder
    public History(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public void addUrl(Url url) {
        this.urls.add(url);
    }
}