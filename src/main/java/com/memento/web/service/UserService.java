package com.memento.web.service;

import com.memento.web.common.RedisId;
import com.memento.web.common.TabUrl;
import com.memento.web.domain.*;
import com.memento.web.dto.HistoryRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private HashOperations<String, String, TabUrl> hashOperations;
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final RedisTemplate<String, TabUrl> redisTemplate;

    public UserService(UserRepository userRepository, HistoryRepository historyRepository, RedisTemplate<String, TabUrl> redisTemplate) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    public void saveUser(Integer hashCode,String name) {
        if (!userRepository.findByName(name).isPresent()) {
            userRepository.save(User.builder().id(getObjectId()).name(name).build());
        }
    }
    private String getObjectId() {
        return ObjectId.get().toString();
    }

    public void saveToMainDB(String name) {
        User currentUser = userRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("User Not Founded!"));
        List<History> historyList = new ArrayList<>(currentUser.getHistoryList());
        Integer suffix = 1;
        while(true) {
            String redisId = createRedisId(name, suffix);
            Set<String> keySet = hashOperations.keys(redisId);
            if (keySet.size() == 0) {
                break;
            } else {
                History sameKeywordHistory = historyList.stream()
                        .filter( h -> h.getKeyword().equals(getCurrentKeyword(redisId)))
                        .findFirst().orElse(History.builder().keyword(getCurrentKeyword(redisId))
                                                                                      .id(getObjectId())
                                                                                      .build());
                if (isSearchedKeyword(historyList, sameKeywordHistory)) {
                    historyList.set(historyList.indexOf(sameKeywordHistory), updateUrlInMainDB(sameKeywordHistory,redisId));
                } else {
                    historyList.add(updateUrlInMainDB(sameKeywordHistory, redisId));
                }

                keySet.forEach(key -> hashOperations.delete(redisId, key));
            }
            suffix++;
        }

        historyList.forEach(historyRepository::save);
        currentUser.updateHistoryList(historyList);
        userRepository.save(currentUser);
    }

    private boolean isSearchedKeyword(List<History> historyList, History currentHistory) {
        return historyList.indexOf(currentHistory) >= 0;
    }

    private History updateUrlInMainDB(History history, String redisId) {
        List<Url> newUrls = hashOperations.keys(redisId).stream()
                .map(address -> hashOperations.get(redisId, address))
                .map(this::dtoToUrl)
                .peek(url -> {
                    if (isVisitedUrl(history.getUrls(),url)) {
                        url.addVisitedCount(getCurrentVisitedCount(history.getUrls(), url));
                    }
                })
                .collect(Collectors.toList());
        ;
        history.getUrls().removeIf(url -> isVisitedUrl(newUrls, url));

        newUrls.forEach(history::addUrl);
        return history;
    }

    private boolean isVisitedUrl(List<Url> urls, Url currentUrl) {
        return urls.stream().anyMatch(u -> u.getAddress().equals(currentUrl.getAddress()));
    }

    private Integer getCurrentVisitedCount(List<Url> urls, Url url) {
        return urls.stream().filter(u -> u.getAddress().equals(url.getAddress()))
                .map(Url::getVisitedCount).collect(Collectors.toList()).get(0);
    }

    private String getCurrentKeyword(String redisId) {
        return hashOperations.keys(redisId).stream()
                .findAny()
                .map( k -> hashOperations.get(redisId,k))
                .map(TabUrl::getKeyword).get();
    }

    private String createRedisId(String name, int i) {
        return name + "+" + i;
    }

    private Url dtoToUrl(TabUrl dto) {
        return Url.builder()
                .address(dto.getAddress())
                .visitedTime(convertToDate(dto.getVisitedTime()))
                .stayedTime(convertToDate(dto.getStayedTime()))
                .visitedCount(dto.getVisitedCount())
                .build();
    }

    private Date convertToDate(Long unixTime) {
        return new Date(unixTime);
    }
}
