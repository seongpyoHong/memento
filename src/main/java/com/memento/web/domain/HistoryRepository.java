package com.memento.web.domain;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends MongoRepository<History, String> {
    Optional<History> findByKeyword(String keyword);
    List<History> findAllByKeywordContaining(String keyword);
}
