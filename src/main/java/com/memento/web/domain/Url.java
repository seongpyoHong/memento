package com.memento.web.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.util.Date;

@ToString
@NoArgsConstructor
@Getter
public class Url {
    private String address;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date visitedTime;

    @Builder
    public Url(String address, Date visitedTime) {
        this.address = address;
        this.visitedTime = visitedTime;
    }
}
