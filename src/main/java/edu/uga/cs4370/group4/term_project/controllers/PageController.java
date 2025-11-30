package edu.uga.cs4370.group4.term_project.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {

    private static final String SITE_NAME = "Fitness Tracker";

    private ModelAndView createModelAndView(String viewName, boolean showNavbar, boolean showProfileIcon) {
        ModelAndView mv = new ModelAndView(viewName);
        mv.addObject("title", SITE_NAME);
        mv.addObject("showNavbar", showNavbar);
        mv.addObject("showProfileIcon", showProfileIcon);
        return mv;
    }

    @GetMapping("/login")
    public ModelAndView login(@RequestParam(name = "error", required = false) String error) {
        return createModelAndView("login", false, false);
    }

    @GetMapping("/register")
    public ModelAndView register(@RequestParam(name = "error", required = false) String error) {
        return createModelAndView("register", false, false);
    }

    @GetMapping("/home")
    public ModelAndView home(@RequestParam(name = "error", required = false) String error) {
        return createModelAndView("home", true, true);
    }

    @GetMapping("/profile")
    public ModelAndView profile(@RequestParam(name = "error", required = false) String error) {
        return createModelAndView("profile", true, true);
    }

    @GetMapping("/workouts")
    public ModelAndView workouts(@RequestParam(name = "error", required = false) String error) {
        return createModelAndView("workouts", true, true);
    }

    @GetMapping("/exercises")
    public ModelAndView exercises(@RequestParam(name = "error", required = false) String error) {
        return createModelAndView("exercises", true, true);
    }

    @GetMapping("/")
    public ModelAndView def(@RequestParam(name = "error", required = false) String error) {
        return createModelAndView("login", false, false);
    }
}
