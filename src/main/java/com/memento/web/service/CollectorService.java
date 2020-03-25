package com.memento.web.service;

import com.memento.web.domain.History;
import com.memento.web.domain.HistoryRepository;
import com.memento.web.dto.HistoryRequestDto;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CollectorService {
    @Autowired
    private HistoryRepository historyRepository;

    public Mono<String> saveHistory(HistoryRequestDto historyRequestDto) {
        return historyRepository.insert(History.builder().id(getObjectId()).url(historyRequestDto.getUrl()).build())
                .map(History::getUrl);
    }

    private String getObjectId() {
        return ObjectId.get().toString();
    }


}
