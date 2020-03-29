package com.memento.web.controller;

import com.memento.web.dto.HistroyRequestDto2;
import com.memento.web.service.CollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CollectorController {

    @Autowired
    private CollectorService collectorService;


    @PostMapping("/collect")
    public void collectHistory(@RequestBody HistroyRequestDto historyRequestDto) {
        System.out.println(historyRequestDto);
//        collectorService.saveHistory(historyRequestDto);
    }
}
