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
        if(type==SortType.DEFAULT)
            model.addAttribute("historyList",
                    searchService.findAll("test-user", page));
        else if(type==SortType.RECENT)
            model.addAttribute("historyList",
                    searchService.findAllBySortedVisitedTime("test-user", page));
        else if(type==SortType.VISITCOUNT)
            model.addAttribute("historyList",
                    searchService.findAllBySortedVisitedCount("test-user", page));
        else if(type==SortType.STAYING)
            model.addAttribute("historyList",
                    searchService.findAllBySortedStayedTime("test-user", page));
        else
            System.out.println("에러처리 필요");
        return "log";
    }

    @GetMapping("/log/{keyword}")
    public String keywordLog(@RequestParam("page") Long page,
                           @RequestParam("radiobutton") SortType type,
                           @PathVariable String keyword, Model model) {
        if(type==SortType.DEFAULT)
            model.addAttribute("historyList",
                    searchService.findAllByKeyword("test-user", keyword, page));
        else if(type==SortType.RECENT)
            model.addAttribute("historyList",
                    searchService.findAllByKeywordVisitedTime("test-user", keyword, page));
        else if(type==SortType.VISITCOUNT)
            model.addAttribute("historyList",
                    searchService.findAllByKeywordVisitedCount("test-user", keyword, page));
        else if(type==SortType.STAYING)
            model.addAttribute("historyList",
                    searchService.findAllByKeywordStayedTime("test-user", keyword, page));
        else
            System.out.println("에러처리 필요");
        return "log-detail";
    }
}
