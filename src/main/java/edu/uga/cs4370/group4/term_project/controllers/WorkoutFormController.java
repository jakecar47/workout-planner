package edu.uga.cs4370.group4.term_project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.uga.cs4370.group4.term_project.services.WorkoutService;
import edu.uga.cs4370.group4.term_project.services.UserService;

import java.time.LocalDateTime;

/**
 * This controller handles /workout_form URL.
 */
@Controller
@RequestMapping("/workout_form")
public class WorkoutFormController {

    // Used for posting new workout
    private final WorkoutService workoutService;
    private final UserService userService;

    @Autowired
    public WorkoutFormController(WorkoutService workoutService,
    UserService userService) {
        this.workoutService = workoutService;
        this.userService = userService; 
    }

    /**
     * Serves the webpage at /workout_form URL.
     */
    @GetMapping
    public String webpage(Model mode) {

        return "workout_form";
    }

    /**
     * Handles POST request to create a new workout.
     * startTime and endTime would be given for a past workout(?).
     */
    @PostMapping
    public String postNewWorkout(@RequestParam("name") String name,
                                 @RequestParam("description") String description,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime) {
        // Logic to save the new workout using workoutService

        int currentUserId = userService.getLoggedInUser().getId();
        String now = LocalDateTime.parse(startTime).toString();
        String end = LocalDateTime.parse(endTime).toString();
        workoutService.createWorkout(name, currentUserId, description, now, end);

        // Redirect to workouts page after successful creation
        return "redirect:/workouts";

    }
}