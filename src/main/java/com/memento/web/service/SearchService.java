package com.memento.web.service;

import com.memento.web.controller.SearchController;
import com.memento.web.domain.HistoryRepository;
import com.memento.web.domain.UserRepository;
import com.memento.web.dto.HistoryResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private UserRepository userRepository;

    public List<HistoryResponseDto> findAll(String username){
        return userRepository.findAllByNameWithPagination(username, 0, 2)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"))
                .getHistoryList().stream()
                .peek(H -> logger.info(H.toString()))
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }

//    public List<HistoryResponseDto> findAllByKeywordContaining(String user, String keyword){
//
//    }
}
