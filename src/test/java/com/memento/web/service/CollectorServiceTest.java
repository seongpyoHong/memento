package com.memento.web.service;

import com.memento.web.domain.*;
import com.memento.web.dto.HistoryRequestDto;
import org.bson.types.ObjectId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CollectorServiceTest {

    private static final String title1 = "스프링부트 - Google 검색";
    private static final String title2 = "자바 : 네이버 통합검색";

    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollectorService collectorService;
    @Autowired
    private HttpSession session;

    private List<HistoryRequestDto> requestDtoList = new ArrayList<>();
    private String userName = "test-user";

    @BeforeEach
    void setUp() {
        requestDtoList.add(HistoryRequestDto.builder().title(title1).userUrl("http//springboot1.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title("스프링부트-블로그-1").userUrl("http//springboot2.com").lastVisitTime(1585405292000L).visitCount(1).build());
        requestDtoList.add(HistoryRequestDto.builder().title("스프링부트-블로그-2").userUrl("http//springboot3.com").lastVisitTime(1585405292000L).visitCount(1).build());

        //save test user
        userRepository.save(User.builder().email("test@email.com").id(ObjectId.get().toString()).name("test-user").password("password").build());
    }

    @AfterEach
    void tearDown() {
        historyRepository.deleteAll();
        userRepository.deleteAll();
        session.removeAttribute(userName);
    }

    @Test
    void 키워드_파싱_및_트리거_저장() {
        //given
        //when
        collectorService.saveHistory(requestDtoList,userName);
        //then
        List<History> userHistory = userRepository.findByName(userName).get().getHistoryList();
        assertEquals(userHistory.get(0).getKeyword(), "스프링부트");
    }

    @Test
    void 트리거_중복_저장_방지() {
        //given
        collectorService.saveHistory(requestDtoList,userName);
        List<History> beforeUserHistory = userRepository.findByName(userName).get().getHistoryList();
        //when
        collectorService.saveHistory(requestDtoList,userName);
        //then
        List<History> afterUserHistory = userRepository.findByName(userName).get().getHistoryList();
        assertEquals(beforeUserHistory.size(), afterUserHistory.size());
    }
    
    @Test
    void URL_저장(){
        //given
        //when
        collectorService.saveHistory(requestDtoList,userName);
        //then
        History springBootHistory = userRepository.findByName(userName).get().getHistoryList().get(0);
        List<Url> springBootUrl = springBootHistory.getUrls();
        assertEquals(springBootUrl.size(), 2);
        assertEquals(springBootUrl.get(0).getAddress(),"http//springboot2.com" );
        assertEquals(springBootUrl.get(0).getVisitedTime(), new Date(1585405292000L) );
        assertEquals(springBootUrl.get(1).getAddress(), "http//springboot3.com");
        assertEquals(springBootUrl.get(1).getVisitedTime(), new Date(1585405292000L) );
    }
    
    @Test
    void 이전_키워드_캐싱() {
        //given
        collectorService.saveHistory(requestDtoList,userName);
        List<HistoryRequestDto> afterRequestDto = new ArrayList<>();
        afterRequestDto.add(HistoryRequestDto.builder().title("스프링부트-블로그-3").userUrl("http//springboot4.com").lastVisitTime(1585405292000L).visitCount(1).build())
        //when
        collectorService.saveHistory(afterRequestDto,userName);
        //then
        History springBootHistory = userRepository.findByName(userName).get().getHistoryList().get(0);
        List<Url> springBootUrl = springBootHistory.getUrls();
        assertEquals(springBootUrl.size(), 3);
        assertEquals(springBootUrl.get(0).getAddress(),"http//springboot3.com" );
        assertEquals(springBootUrl.get(0).getVisitedTime(), new Date(1585405292000L) );
    }

}