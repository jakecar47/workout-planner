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

    // Toggle complete/incomplete handler
    @PostMapping("/goals/toggle/{id}")
    public String toggleGoal(@PathVariable("id") int id) {
        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }
        User user = userService.getLoggedInUser();
        if (user == null) {
            return "redirect:/login";
        }

        goalService.toggleGoalCompletion(id, user.getId());
        return "redirect:/profile";
    }
    
    /* ------------------------------------------------------
     * CREATE GOAL
     * ------------------------------------------------------ */
    @PostMapping("/goals/new")
    public String createGoal(@RequestParam("description") String description,
                         @RequestParam(value = "exerciseId", required = false) Integer exerciseId,
                         Model model) {
        User user = userService.getLoggedInUser();
        if (user == null) {
            return "redirect:/login";
        }

        if (description == null || description.trim().isEmpty()) {
            model.addAttribute("error", "Description is required");
            model.addAttribute("user", user);
            return "goal_form";
        }

        boolean success = goalService.createGoal(user.getId(), description.trim(), exerciseId);
        
        if (success) {
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "Failed to create goal");
            model.addAttribute("user", user);
            return "goal_form";
        }
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

    /* ------------------------------------------------------
     * SHOW ADD GOAL FORM
     * ------------------------------------------------------ */
    @GetMapping("/goals/new")
    public String showAddGoalForm(Model model) {
        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("user", userService.getLoggedInUser());
        return "goal_form";
    }

}
