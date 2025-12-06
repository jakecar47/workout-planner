package edu.uga.cs4370.group4.term_project.controllers;

import edu.uga.cs4370.group4.term_project.models.User;
import edu.uga.cs4370.group4.term_project.services.UserService;
import edu.uga.cs4370.group4.term_project.services.GoalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class GoalController {

    private final UserService userService;
    private final GoalService goalService;

    @Autowired
    public GoalController(UserService userService, GoalService goalService) {
        this.userService = userService;
        this.goalService = goalService;
    }

    /* ------------------------------------------------------
     * VIEW ALL GOALS 
     * ------------------------------------------------------ */
    @GetMapping("/goals")
    public String goalsPage(Model model) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.getLoggedInUser();

        model.addAttribute("goals",
                goalService.getGoalsForUser(user.getId()));

        return "goals";
    }

    /* ------------------------------------------------------
     * CREATE GOAL
     * ------------------------------------------------------ */
    @PostMapping("/goals/create")
    public String createGoal(@RequestParam String description,
                             @RequestParam(required = false) Integer exerciseId) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        if (description == null || description.isBlank()) {
            return "redirect:/profile";
        }

        User user = userService.getLoggedInUser();

        goalService.createGoal(
                user.getId(),
                description.trim(),
                exerciseId
        );

        return "redirect:/profile";
    }

    /* ------------------------------------------------------
     * DELETE GOAL
     * ------------------------------------------------------ */
    @PostMapping("/goals/delete/{id}")
    public String deleteGoal(@PathVariable int id) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        goalService.deleteGoal(id);
        return "redirect:/profile";
    }
}
