package com.memento.web.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.Id;
import java.util.Date;

@ToString
@NoArgsConstructor
@Getter
@Document(collection = "history")
public class History {
    @Id
    private String id;

    private String url;

    private String keyword;

    private String type;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date visitTime;

    @Builder
    public History(String id, String url, String keyword, String type, Date visitTime) {
        this.id = id;
        this.url = url;
        this.keyword = keyword;
        this.type = type;
        this.visitTime = visitTime;
    }
//    private Date visitedDate;
}
