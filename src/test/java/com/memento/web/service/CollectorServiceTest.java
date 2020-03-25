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

import java.util.stream.Collector;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CollectorServiceTest {

    private static final String URL = "test.com";

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private CollectorService collectorService;

    @Test
    void 데이터_저장_테스트() {

        //given
        TransitionType type = TransitionType.LINK;
        HistoryRequestDto requestDto = new HistoryRequestDto(URL, type);

        //when
        Mono<String> result = collectorService.saveHistory(requestDto);

        //then
        Flux<History> findResult = historyRepository.findAll();
        assertEquals(URL,findResult.blockFirst().getUrl());
        assertEquals(URL,result.block());
    }
}