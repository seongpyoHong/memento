package com.memento.web.controller;

import com.memento.web.dto.HistoryRequestDto;
import com.memento.web.service.CollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CollectorController {

    @Autowired
    private CollectorService collectorService;
    @PostMapping("/collector")
    public void collectHistory(@RequestBody HistoryRequestDto historyRequestDto) {
        collectorService.saveHistory(historyRequestDto);
    }
}
