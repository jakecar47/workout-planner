package edu.uga.cs4370.group4.term_project.controllers;

import edu.uga.cs4370.group4.term_project.models.User;
import edu.uga.cs4370.group4.term_project.models.Workout;
import edu.uga.cs4370.group4.term_project.services.UserService;
import edu.uga.cs4370.group4.term_project.services.WorkoutService;
import edu.uga.cs4370.group4.term_project.services.WorkoutExerciseService;
import edu.uga.cs4370.group4.term_project.services.ExerciseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
     * LIST USER WORKOUTS
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

    // /* ------------------------------------------------------
    //  * SHOW CREATE WORKOUT FORM
    //  * ------------------------------------------------------ */
    // @GetMapping("/workouts/new")
    // public String newWorkoutPage() {

    //     if (!userService.isAuthenticated()) {
    //         return "redirect:/login";
    //     }

    //     return "workout_form";
    // }

    // /* ------------------------------------------------------
    //  * CREATE WORKOUT
    //  * ------------------------------------------------------ */
    // @PostMapping("/workouts/create")
    // public String createWorkout(@RequestParam String name,
    //                              @RequestParam(required = false) String description) {

    //     if (!userService.isAuthenticated()) {
    //         return "redirect:/login";
    //     }

    //     User user = userService.getLoggedInUser();

    //     // Convert to STRING instead of Timestamp
    //     String now = LocalDateTime.now().toString();

    //     workoutService.createWorkout(
    //             name.trim(),
    //             user.getId(),
    //             description == null ? null : description.trim(),
    //             now,
    //             null
    //     );

    //     return "redirect:/workouts";
    // }

    /* ------------------------------------------------------
     * WORKOUT DETAILS PAGE
     * ------------------------------------------------------ */
    @GetMapping("/workouts/{id}")
    public String workoutDetails(@PathVariable int id, Model model) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        Workout workout = workoutService.getWorkoutById(id);
        if (workout == null) {
            return "redirect:/workouts";
        }

        model.addAttribute("workout", workout);
        model.addAttribute("workoutExercises",
                workoutExerciseService.getExercisesForWorkout(id));

        model.addAttribute("allExercises",
                exerciseService.getAllExercises());

        return "workout_details";
    }

    /* ------------------------------------------------------
     * ADD EXERCISE TO WORKOUT
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
     * REMOVE EXERCISE FROM WORKOUT
     * ------------------------------------------------------ */
    @PostMapping("/workouts/{id}/remove-exercise/{exerciseId}")
    public String removeExerciseFromWorkout(@PathVariable int id,
                                             @PathVariable int exerciseId) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

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
