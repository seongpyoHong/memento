package com.memento.web.service;

import com.memento.web.controller.SearchController;
import com.memento.web.domain.*;
import com.memento.web.dto.HistoryResponseDto;
import com.memento.web.dto.SortType;
import com.memento.web.dto.UserResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("Deprecated")
@Service
public class SearchService {
    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private MongoOperations mongoOperations;


    //@Cacheable(value = "all-keyword")
    public List<HistoryResponseDto> findAllByName(String username) {
        List<AggregationOperation> list = new ArrayList<AggregationOperation>();
        list.add(Aggregation.match(Criteria.where("name").is(username)));
        list.add(Aggregation.unwind("historyList"));

        TypedAggregation<User> agg = Aggregation.newAggregation(User.class, list);

        return mongoOperations.aggregate(agg, User.class, UserResponseDto.class)
                .getMappedResults().stream()
                .map(UserResponseDto::getHistoryList)
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }

    //@Cacheable(value = "include-keyword")
    public List<HistoryResponseDto> findAllByKeyword(String username, String search){
        List<AggregationOperation> list = new ArrayList<AggregationOperation>();
        list.add(Aggregation.match(Criteria.where("name").is(username)));
        list.add(Aggregation.unwind("historyList"));
        list.add(Aggregation.match(Criteria.where("historyList.keyword").regex(search)));
        list.add(Aggregation.group("id", "name").push( "historyList").as("historyList"));
        list.add(Aggregation.project("id", "name", "historyList"));
        list.add(Aggregation.unwind("historyList"));

        TypedAggregation<User> agg = Aggregation.newAggregation(User.class, list);

        return mongoOperations.aggregate(agg, User.class, UserResponseDto.class)
                .getMappedResults().stream()
                .map(UserResponseDto::getHistoryList)
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }

    // ------------------- ByUser
    public Page<HistoryResponseDto> findAllByNameWithPageination(String username, SortType type, Pageable pageable){
        List<HistoryResponseDto> responseDtos = findAllByName(username);
        return getPagedResult(responseDtos, type, pageable);
    }

    // ------------------- ByKeyword
    public Page<HistoryResponseDto> findAllByKeywordWithPageination(String username, String search, SortType type, Pageable pageable){
        List<HistoryResponseDto> responseDtos = findAllByKeyword(username, search);
        return getPagedResult(responseDtos, type, pageable);
    }

    public Page<HistoryResponseDto> getPagedResult(List<HistoryResponseDto> responseDtos, SortType type, Pageable pageable){
        // pageable = PageRequest.of(requestIndex, 10); // page request 생성
        int skip = (pageable.getPageNumber()) * pageable.getPageSize();
        int limit = pageable.getPageSize() + skip;
        int totalElements = responseDtos.size();
        if (limit > totalElements) {
            if (skip >= totalElements)
                return null;
            else
                limit = totalElements;
        }
        if (type == SortType.DEFAULT) {
            responseDtos = responseDtos.subList(skip , limit);
        } else if (type == SortType.STAYING) {
            responseDtos = responseDtos.subList(skip , limit);
            responseDtos.forEach(HistoryResponseDto::sortByStayedtime);
        } else if (type == SortType.VISITCOUNT) {
            responseDtos = responseDtos.subList(skip , limit);
            responseDtos.forEach(HistoryResponseDto::sortByVisitedCount);
        } else if (type == SortType.RECENT) {
            responseDtos = responseDtos.subList(skip , limit);
            responseDtos.forEach(HistoryResponseDto::sortByVisitedtime);
        } else {return null;}

        List<HistoryResponseDto> finalResponseDtos = responseDtos;
        System.out.println(pageable.toOptional()
                .map(Pageable::getOffset)
                .get());
        return new PageImpl<>(responseDtos, pageable, responseDtos.size());
    }
}
