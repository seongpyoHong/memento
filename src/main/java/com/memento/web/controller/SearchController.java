package com.memento.web.controller;

import com.memento.web.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class SearchController {
    //private Logger logger = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;

    @GetMapping("/log")
    public void index(Model model) {
        model.addAttribute("historyList",searchService.findAll("test-user"));
//        model.addAttribute("histories", searchService.findAll());
//        return "log";
//        return new ResponseEntity<>(searchService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/log/{keyword}")
    public void postsUpdate(@PathVariable String keyword) {
//        model.addAttribute("histories", searchService.findAllByKeywordContaining(keyword));
//        return "log-detail";
//        return new ResponseEntity<>(searchService.findAllByKeywordContaining(keyword), HttpStatus.OK);
    }
}
