package com.memento.web.controller;

import com.memento.web.domain.User;
import com.memento.web.domain.UserRepository;
import com.memento.web.dto.HistoryRequestDto;
import com.memento.web.service.CollectorService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class CollectorController {
    private Logger logger = LoggerFactory.getLogger(CollectorController.class);

    @Autowired
    private CollectorService collectorService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/create-test-user")
    public void createTestUser() {
        userRepository.save(User.builder().email("test@email.com").id(ObjectId.get().toString()).name("test-user").password("password").build());
    }

    @PostMapping("/collect")
    public void collectHistory(@RequestBody List<HistoryRequestDto> historyRequestDtoList) {
        historyRequestDtoList.forEach(historyRequestDto -> logger.info(historyRequestDto.toString()));
        String userName = "test-user";
        collectorService.saveHistory(historyRequestDtoList, "test-user");
    }
}
