package com.memento.web.controller;

import com.memento.web.dto.HistoryResponseDto;
import com.memento.web.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private Logger logger = LoggerFactory.getLogger(CollectorController.class);
    private final SearchService searchService;

    @GetMapping("/log")
    public String index(Model model) {
        model.addAttribute("histories", searchService.findAll());
        return "log";
    }

    @GetMapping("/log/{keyword}")
    public String postsUpdate(@PathVariable String keyword, Model
            model) {
        model.addAttribute("histories", searchService.findAllByKeywordContaining(keyword));
        return "log-detail";
    }
}
