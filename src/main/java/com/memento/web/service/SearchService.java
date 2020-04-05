package com.memento.web.service;

import com.memento.web.controller.SearchController;
import com.memento.web.domain.*;
import com.memento.web.dto.HistoryResponseDto;
import com.memento.web.dto.UserResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private UserRepository userRepository;

    public List<HistoryResponseDto> findAll(String username, Integer page, Integer limit) {
        int skip = (page - 1) * limit;
        return userRepository.findAllByNameWithPagination(username, skip, limit)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"))
                .getHistoryList().stream()
                .peek(H -> logger.info(H.toString()))
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "include-keyword")
    public List<HistoryResponseDto> findAllByKeyword(String username, String search, Long page, Integer limit){

        List<AggregationOperation> list = new ArrayList<AggregationOperation>();
        list.add(Aggregation.match(Criteria.where("name").is(username)));
        list.add(Aggregation.unwind("historyList"));
        list.add(Aggregation.match(Criteria.where("historyList.keyword").regex(search)));
        list.add(Aggregation.group("id", "name").push( "historyList").as("historyList"));
        list.add(Aggregation.project("id", "name", "historyList"));
        // 추가
        list.add(Aggregation.unwind("historyList"));
        list.add(Aggregation.skip((page - 1)*limit));
        list.add(Aggregation.limit(limit));

        TypedAggregation<User> agg = Aggregation.newAggregation(User.class, list);

        return mongoOperations.aggregate(agg, User.class, UserResponseDto.class).getMappedResults().stream()
                .map(UserResponseDto::getHistoryList)
                .map(HistoryResponseDto::new)
                .collect(Collectors.toList());
    }


    public List<HistoryResponseDto> findAllByKeywordVisitedTime(String username, String search, Long page, Integer limit){
        List<HistoryResponseDto> responseDtos = findAllByKeyword(username, search, page, limit);
        responseDtos.forEach(HistoryResponseDto::sortByVisitedtime);
        return responseDtos;
    }

     public List<HistoryResponseDto> findAllByKeywordVisitedCount(String username, String search, Long page, Integer limit){
        List<HistoryResponseDto> responseDtos = findAllByKeyword(username, search, page, limit);
        responseDtos.forEach(HistoryResponseDto::sortByVisitedCount);
        return responseDtos;
    }

    public List<HistoryResponseDto> findAllByKeywordStayedtime(String username, String search, Long page, Integer limit){
        List<HistoryResponseDto> responseDtos = findAllByKeyword(username, search, page, limit);
        responseDtos.forEach(HistoryResponseDto::sortByStayedtime);
        return responseDtos;
    }
}
