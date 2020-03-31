package com.memento.web.domain;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends MongoRepository<History, String> {
    Optional<History> findByKeyword(String keyword);
    Page<History> findAllByKeywordContaining(String keyword, Pageable pageable);
}
