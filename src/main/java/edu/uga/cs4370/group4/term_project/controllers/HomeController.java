package edu.uga.cs4370.group4.term_project.controllers;

import edu.uga.cs4370.group4.term_project.models.User;
import edu.uga.cs4370.group4.term_project.services.UserService;
import edu.uga.cs4370.group4.term_project.services.ExerciseService;
import edu.uga.cs4370.group4.term_project.services.WeeklyPlanService;
import edu.uga.cs4370.group4.term_project.services.WorkoutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final UserService userService;
    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final WeeklyPlanService weeklyPlanService;

    @Autowired
    public HomeController(UserService userService,
                          WorkoutService workoutService,
                          ExerciseService exerciseService,
                          WeeklyPlanService weeklyPlanService) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
        this.weeklyPlanService = weeklyPlanService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.getLoggedInUser();
        model.addAttribute("user", user);

        // provide workouts for selects
        var workouts = workoutService.getWorkoutsForUser(user.getId());
        model.addAttribute("workouts", workouts);

        // build schedule map from service
        var schedule = weeklyPlanService.getScheduleForUser(user.getId()); // Map<String, Map<String,Object>>

        // build dayRows so Mustache can access per-day fields directly
        var days = Arrays.asList("MON","TUE","WED","THU","FRI","SAT","SUN");
        var workoutNameById = workouts.stream()
                .collect(Collectors.toMap(w -> w.getId(), w -> w.getName()));

        var dayRows = new ArrayList<Map<String,Object>>();
        for (String d : days) {
            Map<String,Object> row = new HashMap<>();
            row.put("key", d);
            Map<String,Object> entry = schedule.get(d);
            if (entry != null) {
                Object wid = entry.get("workoutId");
                Integer workoutId = (wid instanceof Number) ? ((Number) wid).intValue() : null;
                row.put("workoutId", workoutId);
                row.put("workoutName", workoutId == null ? null : workoutNameById.get(workoutId));
                row.put("notes", entry.get("notes"));
            } else {
                row.put("workoutId", null);
                row.put("workoutName", null);
                row.put("notes", null);
            }
            dayRows.add(row);
        }
        model.addAttribute("dayRows", dayRows);

        return "home";
    }

    /**
     * Get the weekly plan for the logged in user as JSON.
     */
    @GetMapping("/weekly-plan")
    @ResponseBody
    public ResponseEntity<?> getWeeklyPlan() {
        if (!userService.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        User user = userService.getLoggedInUser();
        Map<String, Map<String, Object>> plan = weeklyPlanService.getScheduleForUser(user.getId());
        return ResponseEntity.ok(plan);
    }

    /**
     * Generic save endpoint (keeps backwards compat). Saves workoutId and notes.
     * Expected JSON body: { "day": "MON", "workoutId": 5, "notes": "Legs" }
     */
    @PostMapping(path = "/weekly-plan", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> saveWeeklyPlanEntry(@RequestBody Map<String, Object> body) {
        if (!userService.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        User user = userService.getLoggedInUser();
        String day = (String) body.get("day");
        Object workoutIdObj = body.get("workoutId");
        Integer workoutId = null;
        if (workoutIdObj instanceof Number) {
            workoutId = ((Number) workoutIdObj).intValue();
        } else if (workoutIdObj instanceof String && !((String)workoutIdObj).isEmpty()) {
            try { workoutId = Integer.parseInt((String) workoutIdObj); } catch (NumberFormatException ignored) {}
        }
        String notes = body.get("notes") == null ? null : body.get("notes").toString();

        if (day == null || day.isBlank()) {
            return ResponseEntity.badRequest().body("Missing 'day' field");
        }

        weeklyPlanService.saveEntry(user.getId(), day, workoutId, notes);

        return ResponseEntity.ok(Map.of("status","saved"));
    }

    /**
     * Update only notes for a given day.
     * Body: { "day":"MON", "notes":"..." }
     */
    @PostMapping(path = "/weekly-plan/notes", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateNotes(@RequestBody Map<String, Object> body) {
        if (!userService.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        User user = userService.getLoggedInUser();
        String day = (String) body.get("day");
        String notes = body.get("notes") == null ? "" : body.get("notes").toString();

        if (day == null || day.isBlank()) {
            return ResponseEntity.badRequest().body("Missing 'day' field");
        }

        weeklyPlanService.updateNotes(user.getId(), day, notes);
        return ResponseEntity.ok(Map.of("status","notes_saved"));
    }

    /**
     * Clear assigned workout and notes for a given day (used by Clear button).
     * Body: { "day":"MON" }
     */
    @PostMapping(path = "/weekly-plan/clear", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> clearWeeklyPlanEntry(@RequestBody Map<String, Object> body) {
        if (!userService.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        User user = userService.getLoggedInUser();
        String day = (String) body.get("day");

        if (day == null || day.isBlank()) {
            return ResponseEntity.badRequest().body("Missing 'day' field");
        }

        weeklyPlanService.clearEntry(user.getId(), day);
        return ResponseEntity.ok(Map.of("status","cleared"));
    }
}