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
import org.springframework.web.context.annotation.RequestScope;

import javax.websocket.server.PathParam;

@Controller
@RequiredArgsConstructor
public class SearchController {
    //private Logger logger = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;
    private final Integer defaultPageLimit = 10;

    @GetMapping("/log")
    public void log(@RequestParam("name") String name, @RequestParam("page")Integer page, Model model) {
        model.addAttribute("historyList",
                searchService.findAll(name, page, defaultPageLimit));
        model.addAttribute("userName" , name);
        model.addAttribute("page",page);
//        model.addAttribute("histories", searchService.findAll());
//        return "log";
//        return new ResponseEntity<>(searchService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public String keywordlog(@RequestParam("page") Long page,
                           @RequestParam("type") String type,
                           @RequestParam("keyword") String keyword,
                           @RequestParam("name") String name,  Model model) {
        if(type.equals(SortType.DEFAULT.getName())) {
            model.addAttribute("historyList",
                    searchService.findAllByKeyword(name, keyword, page, defaultPageLimit));
            model.addAttribute("page",page);
            model.addAttribute("userName" , name);
            model.addAttribute("type", type);
        }
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
        return "log";
    }

    @GetMapping("/log-detail")
    public String viewDetail (@RequestParam("page") Long page,
                                @RequestParam("type") String type,
                                @RequestParam("keyword") String keyword,
                                @RequestParam("name") String name, Model model) {
        model.addAttribute("page",page);
        model.addAttribute("userName" , name);
        model.addAttribute("keyword", keyword);
        model.addAttribute("history",
                searchService.findByKeywordInCache(name, keyword, page,defaultPageLimit,type));


        return "log-detail";
    }

}
