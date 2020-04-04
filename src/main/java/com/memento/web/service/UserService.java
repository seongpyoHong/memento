package com.memento.web.service;

import com.memento.web.common.RedisId;
import com.memento.web.common.TabUrl;
import com.memento.web.domain.History;
import com.memento.web.domain.HistoryRepository;
import com.memento.web.domain.User;
import com.memento.web.domain.UserRepository;
import com.memento.web.dto.HistoryRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private HashOperations<String, String, TabUrl> hashOperations;
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final RedisTemplate<String, TabUrl> redisTemplate;

    public UserService(UserRepository userRepository, HistoryRepository historyRepository, RedisTemplate<String, TabUrl> redisTemplate) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    public void saveUser(Integer hashCode,String name) {
        if (!userRepository.findByName(name).isPresent()) {
            userRepository.save(User.builder().id(getObjectId()).name(name).build());
        }
    }
    private String getObjectId() {
        return ObjectId.get().toString();
    }

    public void saveToMainDB(String name) {
//        User currentUser = userRepository.findByName(name)
//                .orElseThrow(() -> new IllegalArgumentException("User Not Founded!"));
//        List<History> historyList = new ArrayList<>(currentUser.getHistoryList());
//        Integer i = 1;
//        while(1) {
//            String redisId = createRedisId(name, 1);
//            if (hashOperations.keys(redisId).size() == 0) {
//                break;
//            } else {
//                hashOperations.keys(redisId).stream().forEach(key -> );
//            }
//        }
    }

    private String createRedisId(String name, int i) {
        return name + "+" + i;
    }
}
