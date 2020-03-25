package com.memento.web.service;

import com.memento.web.common.TransitionType;
import com.memento.web.domain.History;
import com.memento.web.domain.HistoryRepository;
import com.memento.web.dto.HistoryRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Collector;

import static org.junit.jupiter.api.Assertions.*;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CollectorServiceTest {

    private static final String URL1 = "https://www.google.com/search?q=search+ing&oq=search+ing&aqs=chrome..69i57j35i39l2j0l5.1866j0j8&sourceid=chrome&ie=UTF-8";
    private static final String URL2 = "https://www.google.com/search?sxsrf=ALeKk02bCzZCiGppQ9x2QA2AZ3YpoxKgHg%3A1585142690503&ei=olt7XpqrHoXmwQPe4aXoBg&q=search_no_blank&oq=search_no_blank&gs_l=psy-ab.3...89208.93651..93982...2.0..0.169.2335.0j16......0....1..gws-";
    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private CollectorService collectorService;

    @Test
    void 데이터_저장_테스트() {

        //given
        TransitionType type = TransitionType.LINK;
        HistoryRequestDto requestDto = new HistoryRequestDto(URL1, type);

        //when
        Mono<String> result = collectorService.saveHistory(requestDto);

        //then
        Flux<History> findResult = historyRepository.findAll();
        assertEquals(URL1, Objects.requireNonNull(findResult.blockFirst()).getUrl());
        assertEquals(URL1,result.block());
    }

    @Test
    void 키워드_추출_테스트() {
        //given
        String startDelim1 = "&q=";
        String startDelim2 = "?q=";
        String endDelim = "&oq=";

        boolean isContainStartDelim1 = URL2.contains(startDelim1) && URL2.contains(endDelim) ;
        boolean isContainStartDelim2 = URL1.contains(startDelim2) && URL1.contains(endDelim);
        //TODO: + -> % // 1%2 ->1%152
        if (isContainStartDelim1) {
            String keyword = URL2.substring(URL2.indexOf(startDelim1)+3, URL2.indexOf(endDelim));
            assertEquals("search_no_blank",keyword);
        }

        if (isContainStartDelim2) {
            String keyword = URL1.substring(URL1.indexOf(startDelim2)+3, URL1.indexOf(endDelim));
            assertEquals("search ing",convertToOriginString(keyword));
         }

        String key = "2%25+1";
        assertEquals("2% 1",convertToOriginString(key) );
    }

    private String convertToOriginString(String keyword) {
        return keyword.replace("+", " ")
                .replace("%2B","+")
                .replace("%25","%");
    }
}