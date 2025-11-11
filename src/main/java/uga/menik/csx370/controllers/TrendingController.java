package uga.menik.csx370.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.services.TrendingService;

@Controller
@RequestMapping("/trending")
public class TrendingController {

    private final TrendingService trendingService;

    @Autowired
    public TrendingController(TrendingService trendingService) {
        this.trendingService = trendingService;
    }

    @GetMapping()
    public ModelAndView webpage(@RequestParam(name = "limit", required = false) Integer limit) {
        ModelAndView mv = new ModelAndView("trending");
        System.out.println("Running TrendingController"); // debug print
        int lim = (limit == null || limit <= 0) ? 10 : Math.min(limit, 50);

        // Add debug logging
        var tags = trendingService.getTrendingTagsLast7Days(lim);
        System.out.println("Number of trending tags retrieved: " + (tags != null ? tags.size() : "null"));
        if (tags != null) {
            tags.forEach(tag -> System.out.println("Tag: " + tag.getTag() +
                    ", Count: " + tag.getPostCount() +
                    ", Last Used: " + tag.getLastUsed()));
        }

        mv.addObject("tags", tags);
        mv.addObject("limit", lim);
        return mv;
    }
}
