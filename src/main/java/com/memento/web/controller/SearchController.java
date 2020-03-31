package com.memento.web.controller;

import com.memento.web.dto.HistoryResponseDto;
import com.memento.web.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {
    //private Logger logger = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;

    @GetMapping("/log")
    public ResponseEntity<List<HistoryResponseDto>> index(Model model) {
        searchService.findAll();
//        model.addAttribute("histories", searchService.findAll());
//        return "log";
        return new ResponseEntity<>(searchService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/log/{keyword}")
    public ResponseEntity<List<HistoryResponseDto>> postsUpdate(@PathVariable String keyword, Model
            model) {
//        model.addAttribute("histories", searchService.findAllByKeywordContaining(keyword));
//        return "log-detail";
        return new ResponseEntity<>(searchService.findAllByKeywordContaining(keyword), HttpStatus.OK);
    }
}
