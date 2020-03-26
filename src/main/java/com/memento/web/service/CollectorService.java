package com.memento.web.service;

import com.memento.web.domain.History;
import com.memento.web.domain.HistoryRepository;
import com.memento.web.dto.HistoryRequestDto;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class CollectorService {
    private static final String startStr1 = "&q=";
    private static final String startStr2 = "?q=";
    private static final String endStr = "&oq=";

    @Autowired
    private HistoryRepository historyRepository;

    public Mono<History> saveHistory(HistoryRequestDto historyRequestDto) {
        String url = historyRequestDto.getUrl();
        String type = historyRequestDto.getType();
        String keyword = getKeyword(url);
        Date visitTime = convertToDate(historyRequestDto.getVisitTime());

        return historyRepository.insert(History.builder()
                                                .id(getObjectId())
                                                .type(type)
                                                .url(url)
                                                .keyword(keyword)
                                                .visitTime(visitTime).build());
    }

    private String getObjectId() {
        return ObjectId.get().toString();
    }

    private Date convertToDate(Long unixTime) {
        return new Date(unixTime * 1000);
    }
    private boolean isContainStartStr1(String URL) {
        return URL.contains(startStr1);
    }

    private boolean isContainStartStr2(String URL) {
        return URL.contains(startStr2);
    }

    private String getKeyword(String URL) {
        String keyword;
        if (isContainStartStr1(URL)) {
            keyword = URL.substring(URL.indexOf(startStr1)+3, URL.indexOf(endStr));
        } else {
            keyword = URL.substring(URL.indexOf(startStr2)+3, URL.indexOf(endStr));
        }
        return getOriginStr(keyword);
    }

    private String getOriginStr(String keyword) {
        return keyword.replace("+", " ")
                .replace("%2B","+")
                .replace("%25","%");
    }
}