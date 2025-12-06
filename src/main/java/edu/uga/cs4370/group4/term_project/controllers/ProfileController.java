package edu.uga.cs4370.group4.term_project.controllers;

import edu.uga.cs4370.group4.term_project.models.Goal;
import edu.uga.cs4370.group4.term_project.models.User;
import edu.uga.cs4370.group4.term_project.services.UserService;
import edu.uga.cs4370.group4.term_project.services.GoalService;
import edu.uga.cs4370.group4.term_project.services.ExerciseService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    private final UserService userService;
    private final GoalService goalService;
    private final ExerciseService exerciseService;

    @Autowired
    public ProfileController(UserService userService,
                             GoalService goalService,
                             ExerciseService exerciseService) {
        this.userService = userService;
        this.goalService = goalService;
        this.exerciseService = exerciseService;
    }

    /* ------------------------------------------------------
     * PROFILE PAGE
     * ------------------------------------------------------ */
    @GetMapping("/profile")
    public String profilePage(Model model) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.getLoggedInUser();
        model.addAttribute("user", user);

        // Load user's goals
        model.addAttribute("goals",
                goalService.getGoalsForUser(user.getId()));

        // Load exercises for goal assignment dropdown
        model.addAttribute("exercises",
                exerciseService.getAllExercises());

        return "profile";
    }

    /* ------------------------------------------------------
     * ADD GOAL
     * ------------------------------------------------------ */
    @PostMapping("/profile/add-goal")
    public String addGoal(@RequestParam String description,
                          @RequestParam(required = false) Integer exerciseId) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.getLoggedInUser();

        if (description == null || description.isBlank()) {
            return "redirect:/profile"; // Could add an error message later
        }

        goalService.createGoal(user.getId(), description.trim(), exerciseId);

        return "redirect:/profile";
    }
}
