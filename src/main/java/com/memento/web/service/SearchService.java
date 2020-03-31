package com.memento.web.service;

import com.memento.web.controller.SearchController;
import com.memento.web.domain.History;
import com.memento.web.domain.HistoryRepository;
import com.memento.web.dto.HistoryResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private HistoryRepository historyRepository;

    public List<HistoryResponseDto> findAll(String user){
        Page<History> page = historyRepository.findAll(PageRequest.of(0, 10));
        page.forEach(historyRequestDto -> logger.info(page.toString()));
        return page.getContent().stream()
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<HistoryResponseDto> findAllByKeywordContaining(String user, String keyword){
        Page<History> page = historyRepository.findAllByKeywordContaining(keyword,
                PageRequest.of(0, 10));
        page.forEach(historyRequestDto -> logger.info(page.toString()));
        return page.getContent().stream()
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }
}
