package edu.uga.cs4370.group4.term_project.controllers;

import edu.uga.cs4370.group4.term_project.models.Exercise;
import edu.uga.cs4370.group4.term_project.models.User;
import edu.uga.cs4370.group4.term_project.models.Workout;
import edu.uga.cs4370.group4.term_project.models.WorkoutExercise;
import edu.uga.cs4370.group4.term_project.services.UserService;
import edu.uga.cs4370.group4.term_project.services.WorkoutService;
import edu.uga.cs4370.group4.term_project.services.WorkoutExerciseService;
import edu.uga.cs4370.group4.term_project.services.ExerciseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Controller
public class WorkoutController {

    private final UserService userService;
    private final WorkoutService workoutService;
    private final WorkoutExerciseService workoutExerciseService;
    private final ExerciseService exerciseService;

    @Autowired
    public WorkoutController(UserService userService,
                             WorkoutService workoutService,
                             WorkoutExerciseService workoutExerciseService,
                             ExerciseService exerciseService) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.workoutExerciseService = workoutExerciseService;
        this.exerciseService = exerciseService;
    }

    /* ------------------------------------------------------
     * Serves the webapage at /workouts URL.
     * ------------------------------------------------------ */
    @GetMapping("/workouts")
    public String workoutsPage(Model model) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.getLoggedInUser();

        List<Workout> workouts =
                workoutService.getWorkoutsForUser(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("workouts", workouts);

        return "workouts";
    }

     /**
     * Serves the webpage at /workout_form URL.
     */
    @GetMapping("/workout_form")
    public String webpage(Model model,
                          @RequestParam(value = "error", required = false) String error) {

        model.addAttribute("error", error);

        return "workout_form";
    }

    /**
     * Handles POST requests to /workout_form.
     * Redirects to /workouts upon successful creation.
     */
    @PostMapping("/workout_form")
    public String postNewWorkout(@RequestParam("name") String name,
                                 @RequestParam("description") String description,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime) {
        System.out.println("postNewWorkout() called");
        int currentUserId = userService.getLoggedInUser().getId();
        String currentUserName = userService.getLoggedInUser().getUname();
        String now = "";
        if (!startTime.equals("")) {
            System.out.println("startTime: " + startTime);
            now = LocalDateTime.parse(startTime).toString();
        } 
        String end = "";
        if (!endTime.equals("")) {
            System.out.println("endTime: " + endTime);
            end = LocalDateTime.parse(endTime).toString();
        }
        // call to workoutService to create workout
        Boolean success = false;
        try { 
            if (now.equals("") && end.equals("")) {
                System.out.println(currentUserName + "(" + currentUserId + ")" + " is attempting to create a new workout: " + name 
                    + ", " + description);
                if (workoutService.createWorkout(name, currentUserId, description)) {
                    success = true;
                }
                
            } else {
                System.out.println(currentUserName + "(" + currentUserId + ")" + " is attempting to create a new workout: " + name 
                    + ", " + description + ", " + now + " to " + end);
                if (workoutService.createWorkout(name, currentUserId, description, now, end)) {
                    success = true;
                }
            }
            if (success) {
                System.out.println("Workout created successfully");
            }
        } catch (Exception e) {
            System.out.println("Error creating workout: " + e.getMessage());
            String errorMessage = URLEncoder.encode("Error creating workout: ", StandardCharsets.UTF_8);
            return "redirect:/workout_form?error=" + errorMessage;
        }
        
        // Redirect to workouts page after successful creation
        return "redirect:/workouts";

    }

    /* ------------------------------------------------------
     * Serves webpage for /workouts/{id} URL.
     * ------------------------------------------------------ */
    @GetMapping("/workouts/{id}")
    public String workoutDetails(@PathVariable int id, Model model) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.getLoggedInUser();
        model.addAttribute("user", user);

        Workout workout = workoutService.getWorkoutById(id);
        if (workout == null) {
            return "redirect:/workouts";
        }

        List<WorkoutExercise> weList = workoutExerciseService.getExercisesForWorkout(id);
        // System.out.println("Exercises in workout " + id + ":");
        // for (WorkoutExercise we : weList) {
        //     System.out.println(we.getExerciseName());
        // }
        List<Exercise> allExercises = exerciseService.getAllExercises();
        model.addAttribute("workout", workout);
        model.addAttribute("assignedExercises", weList);
        model.addAttribute("allExercises", allExercises);

        return "workout_details";
    }

    /* ------------------------------------------------------
     * Handles POST add exercise to /workouts/{id}/add-exercise.
     * After adding, redirects back to /workouts/{id}.
     * ------------------------------------------------------ */
    @PostMapping("/workouts/{id}/add-exercise")
    public String addExerciseToWorkout(@PathVariable int id,
                                        @RequestParam int exerciseId,
                                        @RequestParam(required = false) Integer sets,
                                        @RequestParam(required = false) Integer reps,
                                        @RequestParam(required = false) Integer time) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        workoutExerciseService.addExerciseToWorkout(
                id, exerciseId, time, sets, reps
        );

        return "redirect:/workouts/" + id;
    }

    /* ------------------------------------------------------
     * Handles POST remove exercise from /workouts/{id}/remove-exercise
     * ------------------------------------------------------ */
    @PostMapping("/workouts/{id}/remove-exercise")
    public String removeExerciseFromWorkout(@PathVariable int id,
                                             @RequestParam int exerciseId) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        System.out.println("Removing exercise " + exerciseId + " from workout " + id);
        workoutExerciseService.removeExercise(id, exerciseId);

        return "redirect:/workouts/" + id;
    }

    /* ------------------------------------------------------
     * DELETE WORKOUT
     * ------------------------------------------------------ */
    @PostMapping("/workouts/delete/{id}")
    public String deleteWorkout(@PathVariable int id) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        workoutService.deleteWorkout(id);
        return "redirect:/workouts";
    }
}
