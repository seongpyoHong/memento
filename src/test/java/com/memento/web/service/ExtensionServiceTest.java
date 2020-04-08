package com.memento.web.service;

import com.memento.web.domain.*;
import com.memento.web.dto.HistoryRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ExtensionServiceTest {
    private final String testUserName = "test-user";
    private final String redisID = "test-user+1";
    private final String defaultkeyword = "test-keyword";
    @Autowired
    private ExtensionService extensionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private RedisTemplate<String, TabUrl> redisTemplate;
    private HashOperations<String, String, TabUrl> hashOperations;

    @BeforeEach
    void setUp() {
        hashOperations = redisTemplate.opsForHash();
    }

    @AfterEach
    void tearDown() {
        historyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void redis_비어있는_경우() {
        //given
        defaultMainDBSettings();
        //when

        extensionService.saveToMainDB(testUserName);
        //then
        History history = userRepository.findByName(testUserName).orElseThrow(() -> new IllegalArgumentException("Not Found in Main DB"))
                                                                .getHistoryList().get(0);
        List<Url> urlList = history.getUrls().stream()
                                            .sorted(Comparator.comparing(Url::getAddress))
                                            .collect(Collectors.toList());

        assertEquals(1, urlList.size());
    }

    @Test
    void 키워드_이미_존재() {
        //given
        defaultMainDBSettings();
        saveHistoryToRedis(defaultkeyword);
        //when
        extensionService.saveToMainDB(testUserName);
        //then
        History history = userRepository.findByName(testUserName).orElseThrow(() -> new IllegalArgumentException("Not Found in Main DB"))
                .getHistoryList().get(0);
        Url url = history.getUrls().stream()
                .sorted(Comparator.comparing(Url::getAddress))
                .collect(Collectors.toList()).get(0);

        assertEquals(6, url.getVisitedCount());
    }

    @Test
    void 키워드_존재_X() {
        //given
        defaultMainDBSettings();
        String newKeyword = "newKeyword";
        saveHistoryToRedis(newKeyword);
        //when
        extensionService.saveToMainDB(testUserName);
        //then
        List<History> history = userRepository.findByName(testUserName).orElseThrow(() -> new IllegalArgumentException("Not Found in Main DB"))
                .getHistoryList();

        assertEquals(2, history.size());
        assertTrue(history.stream().anyMatch(h ->h.getKeyword().equals(newKeyword)));
        assertTrue(history.stream().anyMatch(h ->h.getKeyword().equals(defaultkeyword)));
    }

    private void defaultMainDBSettings() {
        Url url1 = Url.builder().address("http//springboot1.com")
                .stayedTime(new Date(30000L))
                .visitedCount(5)
                .visitedTime(new Date(1595405292000L)).build();

        History history1 = History.builder().id("testhistoryid").keyword(defaultkeyword).build();
        history1.addUrl(url1);

        User user1 = User.builder().id("testuserid").name(testUserName).build();
        user1.addHistory(history1);
        userRepository.save(user1);
    }

    private void saveHistoryToRedis(String keyword) {
        TabUrl redisUrl= TabUrl.builder().keyword(keyword).address("http//springboot1.com").stayedTime(30000L).visitedCount(1).visitedTime(1595405292000L).build();
        hashOperations.put(redisID, "http//springboot1.com", redisUrl);
    }
}

