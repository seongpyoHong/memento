package com.memento.web.service;

import com.memento.web.domain.*;
import com.memento.web.dto.HistoryRequestDto;
import com.memento.web.dto.HistoryResponseDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class SearchServiceTest {
    public static final String user = "test-user";
    // repo di
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        Url url1 = Url.builder().address("http//springboot1.com")
                .stayedTime(new Date(30000L))
                .visitedCount(5)
                .visitedTime(new Date(1595405292000L)).build();

        Url url2 = Url.builder().address("http//springboot2.com")
                .stayedTime(new Date(40000L))
                .visitedCount(1)
                .visitedTime(new Date(1585403292000L)).build();

        Url url3 = Url.builder().address("http//springboot3.com")
                .stayedTime(new Date(10000L))
                .visitedCount(3)
                .visitedTime(new Date(1585405292000L)).build();

        History history1 = History.builder().id("testhistoryid").keyword("test1").build();
        history1.addUrl(url1);
        history1.addUrl(url2);
        history1.addUrl(url3);

        History history2 = History.builder().id("testhistoryid").keyword("test2").build();
        history2.addUrl(url1);
        history2.addUrl(url2);
        history2.addUrl(url3);

        // keyword not contains test
        History history3 = History.builder().id("testhistoryid").keyword("tset3").build();
        history3.addUrl(url1);
        history3.addUrl(url2);
        history3.addUrl(url3);

        History history4 = History.builder().id("testhistoryid").keyword("test4").build();
        history4.addUrl(url1);
        history4.addUrl(url2);
        history4.addUrl(url3);

        User user1 = User.builder().id("testuserid").name("test-user").build();
        user1.addHistory(history1);
        user1.addHistory(history2);
        user1.addHistory(history3);
        user1.addHistory(history4);

        userRepository.save(user1);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // service test
    @Test
    void 키워드_정보_페이지별_반환() {
        List<HistoryResponseDto> responseDtos = searchService.findAll(user, 1, 2);
        List<HistoryResponseDto> responseDtos1 = searchService.findAll(user, 2, 3);
        List<HistoryResponseDto> responseDtos2 = searchService.findAll(user, 3, 1);

        assertEquals(responseDtos.size(), 2);
        assertEquals(responseDtos.get(1).getKeyword(), "test2");
        assertEquals(responseDtos1.size(), 1);
        assertEquals(responseDtos1.get(0).getKeyword(), "test4");
        assertEquals(responseDtos2.size(), 1);
        assertEquals(responseDtos2.get(0).getKeyword(), "tset3");
    }

    @Test
    void 검색어_페이지별_반환() {
        List<HistoryResponseDto> responseDtos = searchService.findAllByKeyword(user, "test", 1L, 3);
        List<HistoryResponseDto> responseDtos1 = searchService.findAllByKeyword(user, "test", 2L, 2);
        List<HistoryResponseDto> responseDtos2 = searchService.findAllByKeyword(user, "test", 1L, 4);

        System.out.println(responseDtos);
        assertEquals(responseDtos.size(), 3);
        assertEquals(responseDtos.get(0).getKeyword(), "test1");
        assertEquals(responseDtos1.size(), 1);
        assertEquals(responseDtos1.get(0).getKeyword(), "test4");
        assertEquals(responseDtos2.size(), 3);
        assertEquals(responseDtos2.get(0).getKeyword(), "test1");
        assertEquals(responseDtos2.get(2).getKeyword(), "test4");
    }
}