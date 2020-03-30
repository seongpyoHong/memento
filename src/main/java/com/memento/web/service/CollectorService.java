package com.memento.web.service;

import com.memento.web.domain.*;
import com.memento.web.dto.HistoryRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CollectorService {
    private static final String googlePostFix = " - Google 검색";
    private static final String naverPostFix = " : 네이버 통합검색";
    private String currentKeyword = null;
    private Logger logger = LoggerFactory.getLogger(CollectorService.class);

    @Autowired
    private HttpSession session;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private UserRepository userRepository;

    public void saveHistory(List<HistoryRequestDto> historyRequestDtoList, String userName) {
        User currentUser = userRepository.findByName(userName)
                .orElseThrow(() -> new IllegalArgumentException("User Not Founded!"));
        List<History> currentUserHistoryList = currentUser.getHistoryList();

        historyRequestDtoList.forEach( historyRequestDto -> {
                if (isTrigger(historyRequestDto.getTitle())) {
                    saveTriggerKeyword(historyRequestDto, currentUserHistoryList);
                    setCurrentKeyword(parseKeyword(historyRequestDto.getTitle()));
                } else {
                    saveUrl(historyRequestDto,currentUserHistoryList);
                }
            }
        );
        userRepository.save(currentUser);
        currentUserHistoryList.forEach(history -> historyRepository.save(history));
        session.setAttribute(userName, currentKeyword);
    }

    private void saveTriggerKeyword(HistoryRequestDto historyRequestDto, List<History> historyList) {
        if (historyList.stream().noneMatch(h -> h.getKeyword().equals(parseKeyword(historyRequestDto.getTitle())))) {
            logger.warn("here? " + historyRequestDto.getTitle());
            historyList.add(dtoToHistory(historyRequestDto));
        }
    }

    private void saveUrl(HistoryRequestDto historyRequestDto, List<History> historyList) {
        String keyword = getCurrentKeyword();
        logger.warn("Current Keyword? " + keyword );
        historyList.stream().filter(h -> h.getKeyword().equals(keyword))
                            .forEach(h -> h.addUrl(Url.builder()
                                                      .address(historyRequestDto.getUserUrl())
                                                      .visitedTime(convertToDate(historyRequestDto.getLastVisitTime()))
                                                      .build()));
    }

    private String getCurrentKeyword() {
        if (currentKeyword != null) {
            return currentKeyword;
        } else {
            return String.valueOf(session.getAttribute("test-user"));
        }
    }

    private void setCurrentKeyword(String keyword) {
        currentKeyword = keyword;
    }

    private History dtoToHistory(HistoryRequestDto historyRequestDto) {
        return History.builder()
                        .id(getObjectId())
                        .keyword(parseKeyword(historyRequestDto.getTitle()))
                        .build();
    }

    private Url dtoToUrl(HistoryRequestDto dto) {
        return Url.builder()
                    .address(dto.getUserUrl())
                    .visitedTime(convertToDate(dto.getLastVisitTime()))
                    .build();
    }

    private boolean isTrigger(String title) {
        return isGoogleSearchForm(title) || isNaverSearchForm(title);
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