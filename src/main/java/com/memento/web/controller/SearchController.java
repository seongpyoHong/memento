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
    //private Logger logger = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;
    private final Integer defaultPageLimit = 2;

    @GetMapping("/log")
    public void log(@PathParam ("page") int page, Model model) {
        model.addAttribute("historyList",
                searchService.findAll("test-user", page, defaultPageLimit));
//        model.addAttribute("histories", searchService.findAll());
//        return "log";
//        return new ResponseEntity<>(searchService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/log/{keyword}")
    public void keywordlog(@RequestParam("page") Long page,
                           @RequestParam("radiobutton") SortType type,
                           @PathVariable String keyword, Model model) {
        if(type==SortType.DEFAULT)
            model.addAttribute("historyList",
                    searchService.findAllByKeyword("test-user", "test", page, defaultPageLimit));
//        else if(type==SortType.RECENT)
//            model.addAttribute("historyList",
//                    searchService.findAllByKeywordVisitedTime("test-user", "test", page, defaultPageLimit));
//        else if(type==SortType.VISITCOUNT)
//            model.addAttribute("historyList",
//                    searchService.findAllByKeywordVisitedCount("test-user", "test", page, defaultPageLimit));
//        else if(type==SortType.STAYING)
//            model.addAttribute("historyList",
//                    searchService.findAllByKeywordStayedtime("test-user", "test", page, defaultPageLimit));
        else
            System.out.println("에러처리 필요");
//        model.addAttribute("histories", searchService.findAllByKeywordContaining(keyword));
//        return "log-detail";
//        return new ResponseEntity<>(searchService.findAllByKeywordContaining(keyword), HttpStatus.OK);
    }
}
