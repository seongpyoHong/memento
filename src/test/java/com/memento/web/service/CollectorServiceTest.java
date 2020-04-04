package com.memento.web.service;

import com.memento.web.common.TabUrl;
import com.memento.web.domain.*;
import com.memento.web.dto.HistoryRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CollectorServiceTest {
    @Autowired
    private CollectorService collectorService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private RedisTemplate<String, TabUrl> redisTemplate;
    private HistoryRequestDto keywordRequestDto;
    private HistoryRequestDto urlRequestDto1;
    private HistoryRequestDto urlRequestDto2;
    private String testUserName = "test-user";
    private Date expectedDate;
    @BeforeEach
    void setUp() {
        //expected Date
        expectedDate = new Date(20000L);
        //Main DB Settings
        Url url1 = Url.builder().address("http//springboot1.com")
                .stayedTime(new Date(30000L))
                .visitedCount(5)
                .visitedTime(new Date(1595405292000L)).build();

        History history1 = History.builder().id("testhistoryid").keyword("test1").build();
        history1.addUrl(url1);

        User user1 = User.builder().id("testuserid").name("test-user").build();
        user1.addHistory(history1);

        //Initialize RequestDTO
        keywordRequestDto = HistoryRequestDto.builder().stayedTime(20000L).tabId(1).title("test1 - Google 검색").url("http//springboot.com").visitedTime(20000L).build();
        urlRequestDto1 = HistoryRequestDto.builder().stayedTime(20000L).tabId(1).title("title1").url("http//springboot2.com").visitedTime(20000L).build();
        userRepository.save(user1);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        historyRepository.deleteAll();
    }

    //TODO: Test Code Refactoring

    @Test
    void 단일탭_저장_테스트_Main_DB_존재_X() {
        //given
        //when
        collectorService.saveHistory(keywordRequestDto, testUserName);
        collectorService.saveHistory(urlRequestDto1, testUserName);
        collectorService.saveHistory(keywordRequestDto, testUserName);
        //then
        History history = userRepository.findByName(testUserName).get().getHistoryList().get(0);
        List<Url> urlList = history.getUrls().stream().sorted(Comparator.comparing(Url::getAddress)).collect(Collectors.toList());

        assertEquals( "test1",history.getKeyword());
        history.getUrls().forEach(url -> System.out.println(url.toString()));
        assertEquals(3, urlList.size());
        assertEquals("http//springboot.com", urlList.get(0).getAddress());
        assertEquals("http//springboot1.com", urlList.get(1).getAddress());
        assertEquals("http//springboot2.com", urlList.get(2).getAddress());
    }

    @Test
    void 단일탭_저장_테스트_Main_DB_존재_O() {
        //given\
        HistoryRequestDto  urlRequestDto = HistoryRequestDto.builder().stayedTime(20000L).tabId(1).title("title1").url("http//springboot1.com").visitedTime(20000L).build();
        //when
        collectorService.saveHistory(keywordRequestDto, testUserName);
        collectorService.saveHistory(urlRequestDto, testUserName);
        collectorService.saveHistory(keywordRequestDto, testUserName);
        //then
        History history = userRepository.findByName(testUserName).get().getHistoryList().get(0);
        List<Url> urlList = history.getUrls().stream().sorted(Comparator.comparing(Url::getAddress)).collect(Collectors.toList());

        assertEquals( "test1",history.getKeyword());
        assertEquals(2, urlList.size());
        assertEquals("http//springboot1.com", urlList.get(1).getAddress());
        assertEquals(expectedDate, urlList.get(1).getStayedTime());
        assertEquals(expectedDate, urlList.get(1).getVisitedTime());
        assertEquals( 6, urlList.get(1).getVisitedCount());
    }

    @Test
    void 단일탭_URL_Redis_업데이트_테스트() {
        //given
        HistoryRequestDto urlRequestDto2 = HistoryRequestDto.builder().stayedTime(10000L).tabId(1).title("title1").url("http//springboot2.com").visitedTime(10000L).build();
        //when
        collectorService.saveHistory(keywordRequestDto, testUserName);
        collectorService.saveHistory(urlRequestDto2, testUserName);
        collectorService.saveHistory(urlRequestDto2, testUserName);
        collectorService.saveHistory(keywordRequestDto, testUserName);
        //then
        History history = userRepository.findByName(testUserName).get().getHistoryList().get(0);
        List<Url> urlList = history.getUrls().stream().sorted(Comparator.comparing(Url::getAddress)).collect(Collectors.toList());
        urlList.forEach(url -> System.out.println(url.toString()));
        assertEquals( "test1",history.getKeyword());
        assertEquals(3, urlList.size());
        assertEquals("http//springboot2.com",urlList.get(2).getAddress());
        assertEquals(new Date(10000L), urlList.get(2).getStayedTime());
        assertEquals(new Date(10000L), urlList.get(2).getVisitedTime());
        assertEquals( 2, urlList.get(2).getVisitedCount());
    }

    @Test
    void 단일탭_Redis_키워드_변경_테스트() {
        //given
        HistoryRequestDto keywordRequestDto2 = HistoryRequestDto.builder().stayedTime(20000L).tabId(1).title("test2 - Google 검색").url("http//spring.com").visitedTime(20000L).build();
        HistoryRequestDto urlRequestDto2 = HistoryRequestDto.builder().stayedTime(10000L).tabId(1).title("title1").url("http//springboot3.com").visitedTime(10000L).build();
        //when
        collectorService.saveHistory(keywordRequestDto, testUserName);
        collectorService.saveHistory(urlRequestDto2, testUserName);
        collectorService.saveHistory(keywordRequestDto2, testUserName);
        collectorService.saveHistory(urlRequestDto2, testUserName);
        collectorService.saveHistory(keywordRequestDto2, testUserName);

        //then
        History historyTest1 = userRepository.findByName(testUserName).get().getHistoryList().get(0);
        History historyTest2 = userRepository.findByName(testUserName).get().getHistoryList().get(1);

        List<Url> urlListTest1 = historyTest1.getUrls().stream().sorted(Comparator.comparing(Url::getAddress)).collect(Collectors.toList());
        List<Url> urlListTest2 = historyTest2.getUrls().stream().sorted(Comparator.comparing(Url::getAddress)).collect(Collectors.toList());

        assertEquals("test1", historyTest1.getKeyword());
        assertEquals("test2", historyTest2.getKeyword());

        assertEquals(3, urlListTest1.size());
        assertEquals(2, urlListTest2.size());

    }

    @Test
    void 다중탭_Redis_저장_테스트() {
        //given
        HistoryRequestDto keywordRequestDto2 = HistoryRequestDto.builder().stayedTime(20000L).tabId(2).title("test2 - Google 검색").url("http//spring.com").visitedTime(20000L).build();
        HistoryRequestDto urlRequestDto2 = HistoryRequestDto.builder().stayedTime(10000L).tabId(2).title("title1").url("http//springboot2.com").visitedTime(10000L).build();
        HistoryRequestDto urlRequestDto3 = HistoryRequestDto.builder().stayedTime(10000L).tabId(1).title("title1").url("http//springboot2.com").visitedTime(10000L).build();

        //when
        collectorService.saveHistory(keywordRequestDto, testUserName);
        collectorService.saveHistory(urlRequestDto1, testUserName);
        collectorService.saveHistory(keywordRequestDto2, testUserName);
        collectorService.saveHistory(urlRequestDto2, testUserName);
        collectorService.saveHistory(urlRequestDto3, testUserName);
        collectorService.saveHistory(keywordRequestDto, testUserName);
        collectorService.saveHistory(keywordRequestDto2, testUserName);

        //then
        History historyTest1 = userRepository.findByName(testUserName).get().getHistoryList().get(0);
        History historyTest2 = userRepository.findByName(testUserName).get().getHistoryList().get(1);

        List<Url> urlListTest1 = historyTest1.getUrls().stream().sorted(Comparator.comparing(Url::getAddress)).collect(Collectors.toList());
        List<Url> urlListTest2 = historyTest2.getUrls().stream().sorted(Comparator.comparing(Url::getAddress)).collect(Collectors.toList());

        assertEquals("test1", historyTest1.getKeyword());
        assertEquals("test2", historyTest2.getKeyword());

        assertEquals(3, urlListTest1.size());
        assertEquals(2, urlListTest2.size());
    }

}


