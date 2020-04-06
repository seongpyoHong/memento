package com.memento.web.controller;

import com.memento.web.dto.SortType;
import com.memento.web.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/log")
    public String log(@PathParam ("page") Long page, SortType type, Model model) {
        return "log";
    }

    @GetMapping("/log/{keyword}")
    public String keywordLog(@RequestParam("page") Long page,
                           @RequestParam("radiobutton") SortType type,
                           @PathVariable String keyword, Model model) {
        return "log-detail";
    }
}
