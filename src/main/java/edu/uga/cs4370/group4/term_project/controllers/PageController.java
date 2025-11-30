package edu.uga.cs4370.group4.term_project.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {

    @GetMapping("/login")
    public ModelAndView login(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("login");
        return mv;
    }

    @GetMapping("/register")
    public ModelAndView register(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("register");
        return mv;
    }

    @GetMapping("/home")
    public ModelAndView home(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("home");
        return mv;
    }

    @GetMapping("/profile")
    public ModelAndView profile(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("profile");
        return mv;
    }

    @GetMapping("/workouts")
    public ModelAndView workouts(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("workouts");
        return mv;
    }

    @GetMapping("/exercises")
    public ModelAndView exercises(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("exercises");
        return mv;
    }

    @GetMapping("/")
    public ModelAndView def(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("login");
        return mv;
    }
}