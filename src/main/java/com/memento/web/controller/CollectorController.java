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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class CollectorController {
    private Logger logger = LoggerFactory.getLogger(CollectorController.class);

    @Autowired
    private CollectorService collectorService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/collect")
    public void collectHistory(@RequestBody HistoryRequestDto historyRequestDto, @RequestParam("name") String name) {
        logger.info(historyRequestDto.toString());
        logger.warn(name);
        collectorService.saveHistory(historyRequestDto, name);
    }
}
