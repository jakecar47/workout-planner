package edu.uga.cs4370.group4.term_project.controllers;

import edu.uga.cs4370.group4.term_project.models.User;
import edu.uga.cs4370.group4.term_project.services.UserService;
import edu.uga.cs4370.group4.term_project.services.ExerciseService;
import edu.uga.cs4370.group4.term_project.services.WorkoutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;
    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;

    @Autowired
    public HomeController(UserService userService,
                          WorkoutService workoutService,
                          ExerciseService exerciseService) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
    }

    /*
    * Serves the root webpage URL.
    */
    @GetMapping("/")
    public String homePage(Model model) {

        // Must be logged in
        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.getLoggedInUser();
        model.addAttribute("user", user);

        // Load workouts for logged-in user
        model.addAttribute("workouts",
                workoutService.getWorkoutsForUser(user.getId()));

        // Load all exercises
        model.addAttribute("exercises",
                exerciseService.getAllExercises());

        // Later weekly planner data can be added here if we want  
        // model.addAttribute("schedule", scheduleService.getWeek(user.getId()));

        return "home";
    }
}
