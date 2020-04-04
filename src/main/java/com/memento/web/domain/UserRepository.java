package com.memento.web.domain;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByName(String name);

    @Query(value = "{'name': ?0}", fields = "{'historyList': {$slice: [?1, ?2]}}")
    Optional<User> findAllByNameWithPagination(String name, int skip, int limit);

//    @Query(value = "{'name': ?0, 'historyList': { $elemMatch: {'keyword': {$regex: ?1, $options:'i'}}}", fields = "{'historyList': {$slice: [?2, ?3]}}")
}
