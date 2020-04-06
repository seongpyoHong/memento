package com.memento.web.service;

import com.memento.web.common.RedisId;
import com.memento.web.common.TabUrl;
import com.memento.web.domain.*;
import com.memento.web.dto.HistoryRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CollectorService {
    private static final String googlePostFix = " - Google 검색";
    private static final String naverPostFix = " : 네이버 통합검색";
    private String currentUserName = null;
    private HashOperations<String, String, TabUrl> hashOperations;
    private Logger logger = LoggerFactory.getLogger(CollectorService.class);

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, TabUrl> redisTemplate;

    public CollectorService(HistoryRepository historyRepository, UserRepository userRepository, RedisTemplate<String, TabUrl> redisTemplate) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    public void saveHistory(HistoryRequestDto requestDto, String userName) {
            currentUserName = userName;

            if (hasKeyword(requestDto) && isNewKeywordTrigger(requestDto)) {
                logger.warn("New Keyword Trigger");
                saveHistoryToMainDB(requestDto);
                deleteHistoryInRedis(requestDto);
                saveNewHistoryInRedis(requestDto);

            } else if (hasKeyword(requestDto))  {
            logger.warn("Has Keyword");
            saveNewHistoryInRedis(requestDto);
        } else {
            logger.warn("Common Url");
            String redisId  = getRedisId(requestDto);
            String hashKey = getHashKey(requestDto);
            if (!hashOperations.hasKey(redisId, hashKey)) {
                logger.warn("Common Url not duplicate");
                hashOperations.put(getRedisId(requestDto), getHashKey(requestDto), getNewUrl(requestDto));
            } else {
                logger.warn("Common Url duplicate");
                TabUrl url = hashOperations.get(redisId,hashKey);
                hashOperations.delete(redisId,hashKey);
                TabUrl updatedUrl = updateUrl(url, requestDto);
                hashOperations.put(redisId, hashKey, updatedUrl);
            }
        }
   }

    private TabUrl updateUrl(TabUrl url, HistoryRequestDto requestDto) {
        logger.warn("Before : " + url.getVisitedCount().toString());
        url.setVisitedCount(url.getVisitedCount()+1);
        logger.warn("After : " + url.getVisitedCount().toString());

        url.setVisitedTime(requestDto.getVisitedTime());
        url.setStayedTime(requestDto.getStayedTime());
        return url;
    }

    private TabUrl getNewUrl(HistoryRequestDto requestDto) {
        return TabUrl.builder()
                .address(requestDto.getUrl())
                .keyword(getCurrentKeyword(getRedisId(requestDto)))
                .stayedTime(requestDto.getStayedTime())
                .visitedCount(1)
                .visitedTime(requestDto.getVisitedTime())
                .build();
    }

    private TabUrl getNewTrigger(HistoryRequestDto requestDto) {
        return TabUrl.builder()
                .address(requestDto.getUrl())
                .keyword(parseKeyword(requestDto.getTitle()))
                .stayedTime(requestDto.getStayedTime())
                .visitedCount(1)
                .visitedTime(requestDto.getVisitedTime())
                .build();
    }

    private void saveNewHistoryInRedis(HistoryRequestDto requestDto) {
        hashOperations.put(getRedisId(requestDto),getHashKey(requestDto), getNewTrigger(requestDto));
    }

    private void deleteHistoryInRedis(HistoryRequestDto requestDto) {
        String redisId = getRedisId(requestDto);
        hashOperations.keys(redisId).forEach(k -> hashOperations.delete(redisId, k));
    }

    private void saveHistoryToMainDB(HistoryRequestDto requestDto) {
        User currentUser = userRepository.findByName(currentUserName)
                .orElseThrow(() -> new IllegalArgumentException("User Not Founded!"));
        String redisId = getRedisId(requestDto);

        List<History> historyList = currentUser.getHistoryList().stream()
                .filter( h -> h.getKeyword().equals(getCurrentKeyword(redisId)))
                .collect(Collectors.toList());

        if (historyList.size() == 1) {
            logger.warn("exist history in mongodb");
            History previousHistory  = historyList.get(0);
            History updatedHistory = updateUrlInMainDB(previousHistory, redisId);

            historyRepository.save(updatedHistory);
            userRepository.save(currentUser);
        } else {
            logger.warn("new history in mongodb");
            History newHistory = saveNewUrlInMainDB(redisId);
            currentUser.addHistory(newHistory);

            historyRepository.save(newHistory);
            userRepository.save(currentUser);
        }
    }

    private History saveNewUrlInMainDB(String redisId) {
        History newHistory = History.builder().keyword(getCurrentKeyword(redisId)).id(getObjectId()).build();
        hashOperations.keys(redisId).stream()
                .map(address -> hashOperations.get(redisId, address))
                .map(this::dtoToUrl)
                .forEach(newHistory::addUrl);

        return newHistory;
    }
    private History updateUrlInMainDB(History history, String redisId) {
        List<Url> newUrls = hashOperations.keys(redisId).stream()
                                    .map(address -> hashOperations.get(redisId, address))
                                    .map(this::dtoToUrl)
                                    .peek(url -> {
                                        if (isVisitedUrl(history.getUrls(),url)) {
                                            url.addVisitedCount(getCurrentVisitedCount(history.getUrls(), url));
                                        }})
                                    .collect(Collectors.toList());
        history.getUrls().removeIf(url -> isVisitedUrl(newUrls, url));

        newUrls.forEach(history::addUrl);
        return history;
    }

    private Integer getCurrentVisitedCount(List<Url> urls, Url url) {
        return urls.stream().filter(u -> u.getAddress().equals(url.getAddress()))
        .map(Url::getVisitedCount).collect(Collectors.toList()).get(0);
    }

    private boolean isVisitedUrl(List<Url> urls, Url currentUrl) {
        return urls.stream().anyMatch(u -> u.getAddress().equals(currentUrl.getAddress()));
    }

    private String getCurrentKeyword(String redisId) {
        return hashOperations.keys(redisId).stream()
                                            .findAny()
                                            .map( k -> hashOperations.get(redisId,k))
                                            .map(TabUrl::getKeyword).orElseThrow(() -> new IllegalArgumentException("Can't Know Keyword. It's Discard..."));
    }

    private boolean isNewKeywordTrigger(HistoryRequestDto requestDto) {
        return hashOperations.entries(getRedisId(requestDto)).size() > 0;
    }

    private String getRedisId(HistoryRequestDto requestDto) {
        return RedisId.builder().tabId(requestDto.getTabId()).userName(currentUserName).build().getId();
    }
    private String getHashKey(HistoryRequestDto requestDto) {
        return requestDto.getUrl();
    }

    private Url dtoToUrl(TabUrl dto) {
        return Url.builder()
                    .address(dto.getAddress())
                    .visitedTime(convertToDate(dto.getVisitedTime()))
                    .stayedTime(convertToDate(dto.getStayedTime()))
                    .visitedCount(dto.getVisitedCount())
                    .build();
    }

    private boolean hasKeyword(HistoryRequestDto requestDto) {
        return isGoogleSearchForm(requestDto.getTitle()) || isNaverSearchForm(requestDto.getTitle());
    }

    private boolean isGoogleSearchForm(String title) {
        int lastIndex = title.length();
        if (lastIndex > googlePostFix.length()) {
            return googlePostFix.equals(title.substring(lastIndex - googlePostFix.length(), lastIndex));
        }
        return false;
    }

    private boolean isNaverSearchForm(String title) {
        int lastIndex = title.length();
        if (lastIndex > naverPostFix.length()) {
            return  naverPostFix.equals(title.substring(lastIndex - naverPostFix.length(), lastIndex));
        }
        return false;
    }

    private String parseKeyword(String title) {
        String keyword = "";
        if (isGoogleSearchForm(title)) {
            keyword = parseTitleinGoole(title);
        }
        else if(isNaverSearchForm(title)) {
            keyword =  parseTitleInNaver(title);
        }
        return keyword;
    }

    private String parseTitleInNaver(String title) {
        return title.substring(0,title.indexOf(naverPostFix));
    }

    private String parseTitleinGoole(String title) {
        return title.substring(0,title.indexOf(googlePostFix));
    }

    private String getObjectId() {
        return ObjectId.get().toString();
    }

    private Date convertToDate(Long unixTime) {
        return new Date(unixTime);
    }
}
