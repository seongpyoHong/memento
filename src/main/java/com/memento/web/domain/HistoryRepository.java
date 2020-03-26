package com.memento.web.domain;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface HistoryRepository extends ReactiveMongoRepository<History, String> {
}
