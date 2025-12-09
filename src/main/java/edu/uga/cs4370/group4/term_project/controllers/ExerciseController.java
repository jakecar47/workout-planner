package edu.uga.cs4370.group4.term_project.controllers;

import edu.uga.cs4370.group4.term_project.models.Exercise;
import edu.uga.cs4370.group4.term_project.services.ExerciseService;
import edu.uga.cs4370.group4.term_project.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final UserService userService;

    @Value("${exercise.image.upload-dir:uploads/exercises}")
    private String uploadDir;

    @Autowired
    public ExerciseController(ExerciseService exerciseService, UserService userService) {
        this.exerciseService = exerciseService;
        this.userService = userService;
    }

    /* ------------------------------------------------------
     * EXERCISES PAGE 
     * ------------------------------------------------------ */
    @GetMapping("/exercises")
    public String exercisesPage(Model model) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("user", userService.getLoggedInUser());

        // ALWAYS BLANK ON LOAD 
        model.addAttribute("exercises", Collections.emptyList());

        return "exercises";
    }

    /* ------------------------------------------------------
     * FUZZY Search
     * ------------------------------------------------------ */
    @GetMapping("/api/exercises/search")
    @ResponseBody
    public List<Exercise> searchExercisesApi(@RequestParam String query) {

        if (query == null || query.isBlank()) {
            return exerciseService.getAllExercises();
        }

        return exerciseService.searchExercises(query.trim());
    }

    /* ------------------------------------------------------
     * SHOW ADD EXERCISE PAGE
     * ------------------------------------------------------ */
    @GetMapping("/exercises/new")
    public String newExercisePage(Model model) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("user", userService.getLoggedInUser());
        return "add_exercise";
    }

    /* ------------------------------------------------------
     * CREATE EXERCISE WITH IMAGE 
     * ------------------------------------------------------ */
    @PostMapping("/exercises/create")
    public String createExercise(
            @RequestParam String name,
            @RequestParam String targetMuscle,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        String imagePath = null;

        if (image != null && !image.isEmpty()) {

            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            image.transferTo(filePath.toFile());

            imagePath = "/uploads/exercises/" + fileName;
        }

        exerciseService.createExercise(
                name.trim(),
                targetMuscle.trim(),
                description.trim(),
                imagePath
        );

        return "redirect:/exercises";
    }

    /* ------------------------------------------------------
     * DELETE EXERCISE
     * ------------------------------------------------------ */
    @PostMapping("/exercises/delete/{id}")
    public String deleteExercise(@PathVariable int id) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        exerciseService.deleteExercise(id);
        return "redirect:/exercises";
    }
}
