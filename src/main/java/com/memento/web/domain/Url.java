package com.memento.web.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;
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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date stayedTime;

    private Integer visitedCount;

    @Builder
    public Url(String address, Date visitedTime, Date stayedTime, Integer visitedCount) {
        this.address = address;
        this.visitedTime = visitedTime;
        this.stayedTime = stayedTime;
        this.visitedCount = visitedCount;
    }

    public void addVisitedCount(Integer count) {
        this.visitedCount += count;
    }
}
