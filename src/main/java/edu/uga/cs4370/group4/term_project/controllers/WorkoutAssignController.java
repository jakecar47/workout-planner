package edu.uga.cs4370.group4.term_project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.uga.cs4370.group4.term_project.models.Workout;
import edu.uga.cs4370.group4.term_project.repositories.WorkoutRepository;
import edu.uga.cs4370.group4.term_project.services.WeeklyPlanService;

@Controller
@RequestMapping("/workouts")
public class WorkoutAssignController {

    private final WorkoutRepository workoutRepository;
    private final WeeklyPlanService weeklyPlanService;

    @Autowired
    public WorkoutAssignController(WorkoutRepository workoutRepository, WeeklyPlanService weeklyPlanService) {
        this.workoutRepository = workoutRepository;
        this.weeklyPlanService = weeklyPlanService;
    }

    // Show the edit/search page; day query param indicates which day we're editing
    @GetMapping("/assign")
    public String assignPage(@RequestParam(name = "day", required = false) String day,
                             Model model,
                             RedirectAttributes ra) {
        if (day == null || day.trim().isEmpty()) {
            ra.addFlashAttribute("error", "No day specified for assigning a workout.");
            return "redirect:/";
        }
        model.addAttribute("day", day);
        model.addAttribute("q", "");
        model.addAttribute("results", List.of());
        return "assign_workout";
    }

    @ModelAttribute
    public void populateUser(Model model) {
        try {
            org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                String username = auth.getName();
                model.addAttribute("user", Collections.singletonMap("uname", username));
                return;
            }
        } catch (Throwable ignored) {
            // fall through if Security not available
        }
        model.addAttribute("user", Collections.singletonMap("uname", ""));
    }
    
    // Perform search and re-render the page with results (preserves day)
    // Perform search and re-render the page with results (preserves day)
    @GetMapping("/search")
    public String search(@RequestParam(name = "q", required = false) String q,
                         @RequestParam(name = "day", required = false) String day,
                         Model model) {

        List<Workout> results;
        if (q == null || q.trim().isEmpty()) {
            results = List.of();
        } else {
            results = workoutRepository.findByNameContainingIgnoreCase(q.trim());
        }

        model.addAttribute("results", results);          // list of Workout objects for template
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("day", day == null ? "" : day);
        return "assign_workout";
    }

    // Assign chosen workout to the user's specified day, then redirect to home
    @PostMapping("/assign")
    public String assignWorkoutToDay(@RequestParam("workoutId") Long workoutId,
                                     @RequestParam("day") String day,
                                     RedirectAttributes ra) {
        try {
            boolean ok = weeklyPlanService.assignWorkoutToDayForCurrentUser(day, workoutId);
            if (ok) {
                ra.addFlashAttribute("msg", "Workout assigned for " + day);
            } else {
                ra.addFlashAttribute("error", "Failed to assign workout for " + day);
            }
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Failed to assign workout: " + ex.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public List<Map<String,Object>> apiSearch(@RequestParam(name = "q", required = false) String q) {
        if (q == null || q.trim().isEmpty()) return List.of();
        var results = workoutRepository.findByNameContainingIgnoreCase(q.trim());
        return results.stream()
                .map(w -> {
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", w.getId());
                    m.put("name", w.getName());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/assign/none")
    @ResponseBody
    public ResponseEntity<?> clearAssignment(@RequestParam("day") String day) {
        try {
            boolean ok = weeklyPlanService.assignWorkoutToDayForCurrentUser(day, 0L);
            return ResponseEntity.ok(java.util.Map.of("ok", ok));
        } catch (Throwable t) {
            return ResponseEntity.status(500).body(java.util.Map.of("ok", false, "error", t.getMessage()));
        }
    }
}