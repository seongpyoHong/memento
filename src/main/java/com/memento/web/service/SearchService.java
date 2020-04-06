package com.memento.web.service;

import com.memento.web.controller.SearchController;
import com.memento.web.domain.*;
import com.memento.web.dto.HistoryResponseDto;
import com.memento.web.dto.SortType;
import com.memento.web.dto.UserResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private MongoOperations mongoOperations;


    //@Cacheable(value = "all-keyword")
    private List<HistoryResponseDto> findAllByName(String username) {
        List<AggregationOperation> list = new ArrayList<AggregationOperation>();
        list.add(Aggregation.match(Criteria.where("name").is(username)));
        list.add(Aggregation.unwind("historyList"));

        TypedAggregation<User> agg = Aggregation.newAggregation(User.class, list);

        return getAggregationResult(agg);
    }

    //@Cacheable(value = "include-keyword")
    private List<HistoryResponseDto> findAllByKeyword(String username, String search){
        List<AggregationOperation> list = new ArrayList<AggregationOperation>();
        list.add(Aggregation.match(Criteria.where("name").is(username)));
        list.add(Aggregation.unwind("historyList"));
        list.add(Aggregation.match(Criteria.where("historyList.keyword").regex(search)));
        list.add(Aggregation.group("id", "name").push( "historyList").as("historyList"));
        list.add(Aggregation.project("id", "name", "historyList"));
        list.add(Aggregation.unwind("historyList"));

        TypedAggregation<User> agg = Aggregation.newAggregation(User.class, list);

        return getAggregationResult(agg);
    }

    private List<HistoryResponseDto> getAggregationResult(TypedAggregation<User> agg) {
        return mongoOperations.aggregate(agg, User.class, UserResponseDto.class)
                .getMappedResults().stream()
                .map(UserResponseDto::getHistoryList)
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }

    public <T> Page<T> getPagedResult(List<T> responseDtos, Pageable pageable){
        Integer totalSize = responseDtos.size();
        int skip = (pageable.getPageNumber()) * pageable.getPageSize();
        int limit = pageable.getPageSize() + skip;
        int totalElements = responseDtos.size();
        if (limit > totalElements) {
            if (skip >= totalElements)
                return null;
            else
                limit = totalElements;
        }
        responseDtos = responseDtos.subList(skip , limit);

        return new PageImpl<>(responseDtos, pageable, totalSize);
    }

    // ------------------- ByUser
    public Page<HistoryResponseDto> findAllByNameWithPageination(String username, Pageable pageable){
        List<HistoryResponseDto> responseDtos = findAllByName(username);
        return getPagedResult(responseDtos, pageable);
    }

    // ------------------- ByKeyword
    public Page<HistoryResponseDto> findAllByKeywordWithPageination(String username, String search, Pageable pageable){
        List<HistoryResponseDto> responseDtos = findAllByKeyword(username, search);
        return getPagedResult(responseDtos, pageable);
    }

    // ------------------- Keyword Detail
    public Page<Url> findOneHistory(String username, String search, SortType type, Pageable pageable) {
        HistoryResponseDto historyResponseDto = findAllByName(username).stream()
                .filter(dto -> dto.getKeyword().equals(search)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Keyword Not Found!"));

        if (type.equals(SortType.STAYING)) {
            historyResponseDto.sortByStayedtime();
        } else if (type.equals(SortType.RECENT)) {
            historyResponseDto.sortByVisitedtime();
        } else if (type.equals(SortType.VISITCOUNT)) {
            historyResponseDto.sortByVisitedCount();
        }

        return getPagedResult(historyResponseDto.getUrls(), pageable);
    }
}