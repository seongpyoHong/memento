package com.memento.web.service;

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
    private static final String title1 = "스프링부트 - Google 검색";
    private static final String title2 = "자바 : 네이버 통합검색";
    private static final String title3 = "몽고DB : 네이버 통합검색";
    private final String user = "test-user";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollectorService collectorService;
    @Autowired
    private SearchService searchService;

    private List<HistoryRequestDto> requestDtoList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        requestDtoList.add(HistoryRequestDto.builder().title(title1).userUrl("http//springboot1.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title("test1").userUrl("http//springboot1-1.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title(title2).userUrl("http//springboot2.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title("test2").userUrl("http//springboot2-1.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title(title3).userUrl("http//springboot3.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title("test3").userUrl("http//springboot3-1.com").lastVisitTime(1585405292000L).visitCount(1).build());

        //save test user
        userRepository.save(User.builder().email("test@email.com").id(ObjectId.get().toString()).name("test-user").password("password").build());
        collectorService.saveHistory(requestDtoList, user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void 키워드_정보_페이지별_반환() {
        List<HistoryResponseDto> responseDtos = searchService.findAll(user, 1, 2);
        List<HistoryResponseDto> responseDtos1 = searchService.findAll(user, 2, 2);
        List<HistoryResponseDto> responseDtos2 = searchService.findAll(user, 3, 1);

        assertEquals(responseDtos.size(), 2);
        assertEquals(responseDtos.get(1).getKeyword(), "자바");
        assertEquals(responseDtos1.size(), 1);
        assertEquals(responseDtos.get(0).getKeyword(), "몽고DB");
        assertEquals(responseDtos2.size(), 1);
        assertEquals(responseDtos.get(0).getKeyword(), "몽고DB");
    }
}