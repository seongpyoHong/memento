package com.memento.web.service;

import com.memento.web.domain.HistoryRepository;
import com.memento.web.dto.HistoryResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private HistoryRepository historyRepository;

    public List<HistoryResponseDto> findAll(){
        return historyRepository.findAll().stream()
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<HistoryResponseDto> findAllByKeywordContaining(String keyword){
        return historyRepository.findAllByKeywordContaining(keyword).stream()
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }
}
