package com.memento.web.service;

import com.memento.web.controller.SearchController;
import com.memento.web.domain.History;
import com.memento.web.domain.HistoryRepository;
import com.memento.web.domain.User;
import com.memento.web.domain.UserRepository;
import com.memento.web.dto.HistoryResponseDto;
import javafx.beans.property.IntegerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    public List<HistoryResponseDto> findAll(String username, Integer page, Integer limit){
        int skip = (page-1)*limit;
        return userRepository.findAllByNameWithPagination(username, skip, limit)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"))
                .getHistoryList().stream()
                .peek(H -> logger.info(H.toString()))
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<HistoryResponseDto> findAllByKeyword(String username, String search, Integer page, Integer limit){
        int skip = (page-1)*limit;
        return userRepository.findAllByNameAndKeywordWithPagination(username, search, skip, limit)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"))
                .getHistoryList().stream()
                .peek(H -> logger.info(H.toString()))
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }
}
