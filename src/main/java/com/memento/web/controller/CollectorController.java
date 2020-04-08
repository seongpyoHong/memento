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

    private final CollectorService collectorService;
    public CollectorController(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    @PostMapping("/collect")
    public void collectHistory(@RequestBody HistoryRequestDto historyRequestDto, @RequestParam("name") String name) {
        collectorService.saveHistory(historyRequestDto, name);
    }
}
