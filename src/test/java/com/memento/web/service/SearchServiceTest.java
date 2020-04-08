package com.memento.web.service;

import com.memento.web.domain.*;
import com.memento.web.dto.HistoryRequestDto;
import com.memento.web.dto.HistoryResponseDto;
import com.memento.web.dto.SortType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                .visitedCount(3)
                .visitedTime(new Date(1595405292000L)).build();

        Url url2 = Url.builder().address("http//springboot2.com")
                .stayedTime(new Date(40000L))
                .visitedCount(1)
                .visitedTime(new Date(1585403292000L)).build();

        Url url3 = Url.builder().address("http//springboot3.com")
                .stayedTime(new Date(10000L))
                .visitedCount(5)
                .visitedTime(new Date(1585405292000L)).build();

        History history1 = History.builder().id("testhistoryid").keyword("test1").build();
        history1.addUrl(url1);
        history1.addUrl(url2);
        history1.addUrl(url3);

        History history2 = History.builder().id("testhistoryid").keyword("test2").build();
        history2.addUrl(url1);
        history2.addUrl(url2);
        history2.addUrl(url3);

        History history3 = History.builder().id("testhistoryid").keyword("tset3").build();
        history3.addUrl(url1);
        history3.addUrl(url2);
        history3.addUrl(url3);

        History history4 = History.builder().id("testhistoryid").keyword("test4").build();
        history4.addUrl(url1);
        history4.addUrl(url2);
        history4.addUrl(url3);

        History history5 = History.builder().id("testhistoryid").keyword("123test").build();
        history5.addUrl(url1);
        history5.addUrl(url2);
        history5.addUrl(url3);

        History history6 = History.builder().id("testhistoryid").keyword("test45").build();
        history6.addUrl(url1);
        history6.addUrl(url2);
        history6.addUrl(url3);

        User user1 = User.builder().id("testuserid").name("test-user").build();
        user1.addHistory(history1);
        user1.addHistory(history2);
        user1.addHistory(history3);
        user1.addHistory(history4);
        user1.addHistory(history5);
        user1.addHistory(history6);

        userRepository.save(user1);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void 로그_페이지별_반환() {
        Pageable pageable1 = PageRequest.of(1, 2);
        Pageable pageable2 = PageRequest.of(1, 3);
        Pageable pageable3 = PageRequest.of(1, 4);
        Pageable pageable4 = PageRequest.of(2, 3);
        Page<HistoryResponseDto> responseDtos1 = searchService.findAllByNameWithPageination(user, pageable1);
        Page<HistoryResponseDto> responseDtos2 = searchService.findAllByNameWithPageination(user, pageable2);
        Page<HistoryResponseDto> responseDtos3 = searchService.findAllByNameWithPageination(user, pageable3);
        Page<HistoryResponseDto> responseDtos4 = searchService.findAllByNameWithPageination(user, pageable4);

        assertEquals(responseDtos1.getTotalElements(), 6);
        assertEquals(responseDtos1.getTotalPages(), 3);
        assertTrue(responseDtos2.isLast());
        assertEquals(responseDtos2.getContent().size(), 3);
        assertEquals(responseDtos2.getContent().get(1).getKeyword(), "123test");
        assertEquals(responseDtos3.getContent().size(), 2);
        assertNull(responseDtos4);
    }

    @Test
    void 검색결과_로그_페이지별_반환() {
        Pageable pageable1 = PageRequest.of(1, 2);
        Pageable pageable2 = PageRequest.of(1, 3);
        Pageable pageable3 = PageRequest.of(1, 4);
        Pageable pageable4 = PageRequest.of(2, 3);
        Page<HistoryResponseDto> responseDtos1 = searchService.findAllByKeywordWithPageination(user, "test", pageable1);
        Page<HistoryResponseDto> responseDtos2 = searchService.findAllByKeywordWithPageination(user, "test", pageable2);
        Page<HistoryResponseDto> responseDtos3 = searchService.findAllByKeywordWithPageination(user, "test", pageable3);
        Page<HistoryResponseDto> responseDtos4 = searchService.findAllByKeywordWithPageination(user, "test", pageable4);

        assertEquals(responseDtos1.getTotalElements(), 5);
        assertEquals(responseDtos1.getTotalPages(), 3);
        assertTrue(responseDtos2.isLast());
        assertEquals(responseDtos2.getContent().size(), 2);
        assertEquals(responseDtos2.getContent().get(1).getKeyword(), "test45");
        assertEquals(responseDtos3.getContent().size(), 1);
        assertNull(responseDtos4);
    }

    @Test
    void 정렬_테스트() {
        Pageable pageable = PageRequest.of(0, 2); // page request 생성

        Page<Url> paggedUrl= searchService.findOneHistory(user, "test45", SortType.DEFAULT, pageable);
        Page<Url> paggedUrl1= searchService.findOneHistory(user, "test45", SortType.RECENT, pageable);
        Page<Url> paggedUrl2= searchService.findOneHistory(user, "test45", SortType.VISITCOUNT, pageable);
        Page<Url> paggedUrl3= searchService.findOneHistory(user, "test45", SortType.STAYING, pageable);

        assertEquals(paggedUrl.getTotalPages(), 2);
        assertEquals(paggedUrl.getTotalElements(), 3);
        assertEquals(paggedUrl.getContent().size(), 2);
        assertTrue(paggedUrl.isFirst());

        assertEquals(paggedUrl1.getContent().get(0).getVisitedCount(), 3);
        assertEquals(paggedUrl2.getContent().get(0).getVisitedCount(), 5);
        assertEquals(paggedUrl3.getContent().get(0).getVisitedCount(), 1);
    }
}