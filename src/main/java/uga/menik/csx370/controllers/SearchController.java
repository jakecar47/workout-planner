package uga.menik.csx370.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uga.menik.csx370.services.SearchService;

@Controller
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public String searchByHashtag(@RequestParam(value = "hashtags", required = false) String hashtags,
                                  Model model) {
        if (hashtags == null || hashtags.trim().isEmpty()) {
            model.addAttribute("query", "");
            model.addAttribute("posts", null);
            return "search";
        }

        String normalized = hashtags.replace("#", " ").trim();
        String[] tags = normalized.split("\\s+");

        model.addAttribute("query", hashtags.trim());
        model.addAttribute("posts", searchService.findExpandedPostsByHashtags(tags));
        return "search";
    }
}
