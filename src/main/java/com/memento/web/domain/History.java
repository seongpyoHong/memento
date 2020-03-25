package com.memento.web.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@NoArgsConstructor
@Getter
@Document(collection = "history")
public class History {
    @Id
    private String id;

    private String url;
    private String keyword;
    @Builder
    public History(String id, String url, String keyword) {
        this.id = id;
        this.url = url;
        this.keyword = keyword;
    }
//    private Date visitedDate;
}
