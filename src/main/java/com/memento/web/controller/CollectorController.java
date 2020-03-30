package com.memento.web.controller;

import com.memento.web.dto.HistoryRequestDto;
import com.memento.web.service.CollectorService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/collect")
    public void collectHistory(@RequestBody List<HistoryRequestDto> historyRequestDtoList) {
        historyRequestDtoList.forEach(historyRequestDto -> logger.info(historyRequestDto.toString()));
        collectorService.saveHistory(historyRequestDtoList);
    }
}
