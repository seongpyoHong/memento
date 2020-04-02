package com.memento.web.service;

import com.memento.web.domain.HistoryRepository;
import com.memento.web.domain.User;
import com.memento.web.domain.UserRepository;
import com.memento.web.dto.HistoryRequestDto;
import com.memento.web.dto.HistoryResponseDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class SearchServiceTest {
    private static final String title1 = "test1 - Google 검색";
    private static final String title2 = "test2 : 네이버 통합검색";
    private static final String title3 = "test3 : 네이버 통합검색";
    private static final String title4 = "4tset : 네이버 통합검색";
    private final String user = "test-user";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private CollectorService collectorService;
    @Autowired
    private SearchService searchService;

    private List<HistoryRequestDto> requestDtoList = new ArrayList<>();



    @BeforeEach
    void setUp() {
        tearDown();
        requestDtoList.add(HistoryRequestDto.builder().title(title1).userUrl("http//springboot1.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title("test1-1").userUrl("http//springboot1-1.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title(title2).userUrl("http//springboot2.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title("test2-1").userUrl("http//springboot2-1.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title(title3).userUrl("http//springboot3.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title("test3-1").userUrl("http//springboot3-1.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title(title4).userUrl("http//springboot4.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title("test4-1").userUrl("http//springboot4-1.com").lastVisitTime(1585405292000L).visitCount(1).build());

        //save test user
        userRepository.save(User.builder().name("test-admin").id(ObjectId.get().toString()).build());
        collectorService.saveHistory(requestDtoList, user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        historyRepository.deleteAll();
    }

    @Test
    void 키워드_정보_페이지별_반환() {
        List<HistoryResponseDto> responseDtos = searchService.findAll(user, 1, 2);
        List<HistoryResponseDto> responseDtos1 = searchService.findAll(user, 2, 2);
        List<HistoryResponseDto> responseDtos2 = searchService.findAll(user, 3, 1);

        assertEquals(responseDtos.size(), 2);
        assertEquals(responseDtos.get(1).getKeyword(), "test2");
        assertEquals(responseDtos1.size(), 2);
        assertEquals(responseDtos1.get(0).getKeyword(), "test3");
        assertEquals(responseDtos2.size(), 1);
        assertEquals(responseDtos2.get(0).getKeyword(), "test3");
    }

    @Test
    void 검색어_페이지별_반환() {
        List<HistoryResponseDto> responseDtos = searchService.findAllByKeyword(user, "test", 1, 2);
        List<HistoryResponseDto> responseDtos1 = searchService.findAllByKeyword(user, "test", 2, 1);
        List<HistoryResponseDto> responseDtos2 = searchService.findAllByKeyword(user, "test", 1, 4);

        assertEquals(responseDtos.size(), 2);
        assertEquals(responseDtos.get(1).getKeyword(), "test2");
        assertEquals(responseDtos1.size(), 1);
        assertEquals(responseDtos1.get(0).getKeyword(), "test2");
        assertEquals(responseDtos2.size(), 3);
        assertEquals(responseDtos2.get(0).getKeyword(), "test1");
        assertEquals(responseDtos2.get(2).getKeyword(), "test3");
    }
}