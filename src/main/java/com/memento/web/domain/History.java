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

    @Builder
    public History(String id, String url) {
        this.id = id;
        this.url = url;
    }
//    private Date visitedDate;
}
