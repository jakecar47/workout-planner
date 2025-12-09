package edu.uga.cs4370.group4.term_project.services;

import edu.uga.cs4370.group4.term_project.models.WeeklyPlan;
import edu.uga.cs4370.group4.term_project.models.Workout;
import edu.uga.cs4370.group4.term_project.repositories.WeeklyPlanRepository;
import edu.uga.cs4370.group4.term_project.repositories.WorkoutRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

@Service
public class WeeklyPlanService {

    private final WeeklyPlanRepository repository;
    private final DataSource dataSource;
    private UserService userService; // for current user lookup
    private WorkoutRepository workoutRepository; // for resolving workout names

    @Autowired
    public WeeklyPlanService(WeeklyPlanRepository repository, DataSource dataSource, UserService userService, WorkoutRepository workoutRepository) {
        this.repository = repository;
        this.dataSource = dataSource;
        this.userService = userService;
        this.workoutRepository = workoutRepository;
    }

    // Ensure the schedule returned to the client includes workoutName (if available)
    public Map<String, Map<String, Object>> getScheduleForUser(int userId) {
        List<WeeklyPlan> rows = repository.findByUserId(userId);
        Map<String, Map<String, Object>> schedule = new HashMap<>();
        for (WeeklyPlan r : rows) {
            Map<String, Object> entry = new HashMap<>();
            Integer wid = r.getWorkoutId(); // workoutIds are ints in DB
            if (wid != null) {
                entry.put("workoutId", wid);
                try {
                    Optional<Workout> maybe = Optional.ofNullable(workoutRepository.getWorkoutById(wid));
                    if (maybe.isPresent()) {
                        entry.put("workoutName", maybe.get().getName());
                    } else {
                        entry.put("workoutName", null);
                    }
                } catch (Throwable ignored) {
                    entry.put("workoutName", null);
                }
            } else {
                entry.put("workoutId", null);
                entry.put("workoutName", null);
            }
            entry.put("notes", r.getNotes());
            schedule.put(r.getDay(), entry);
        }
        return schedule;
    }

    /**
     * Insert or update an entry for the user/day. If workoutId is null it will be stored as 0
     * to satisfy the NOT NULL constraint defined in the table.
     */
    public void saveEntry(int userId, String day, Integer workoutId, String notes) {
        // normalize inputs
        String safeNotes = notes == null ? "" : notes;

        // create WeeklyPlan with nullable workoutId (WeeklyPlan model must use Integer for workoutId)
        WeeklyPlan wp = new WeeklyPlan(null, userId, day, workoutId, safeNotes, null);

        repository.upsert(wp);
    }

    /**
     * Assign a workout (and optional notes) to a day.
     */
    public void assignWorkout(int userId, String day, Integer workoutId, String notes) {
        if (workoutId == null) {
            throw new IllegalArgumentException("workoutId required to assign");
        }
        saveEntry(userId, day, workoutId, notes);
    }

    /**
     * Clear the assigned workout and notes for a user's day.
     * Stores workout_id = 0 and notes = "" (empty string).
     */
    public void clearEntry(int userId, String day) {
        saveEntry(userId, day, null, "");
    }

    /**
     * Update only the notes for a day, preserving any existing workout assignment.
     */
    public void updateNotes(int userId, String day, String notes) {
        Optional<WeeklyPlan> existing = repository.findByUserIdAndDay(userId, day);
        Integer workoutId = existing.map(wp -> wp.getWorkoutId() == 0 ? null : wp.getWorkoutId()).orElse(null);
        saveEntry(userId, day, workoutId, notes);
    }

    /**
     * Delete a day's entry for user.
     */
    public void deleteEntry(int userId, String day) {
        repository.deleteByUserIdAndDay(userId, day);
    }

    
    /** -----------------------------------------------------
     *  UPDATE WORKOUT
     * ----------------------------------------------------- */
    public boolean updateWorkout(int id, String name, String description,
                                 String startTime, String endTime) {

        String sql = """
            UPDATE workouts
            SET name = ?, description = ?, startTime = ?, endTime = ?
            WHERE id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, startTime);
            ps.setString(4, endTime);
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating workout: " + e.getMessage());
            return false;
        }
    }

    /** -----------------------------------------------------
     *  ASSIGN WORKOUT TO DAY FOR CURRENT USER
     *  - Attempts UPDATE first; if no row exists, INSERT new mapping.
     *  - Uses authenticated username to lookup user id in users table.
     * ----------------------------------------------------- */
    public boolean assignWorkoutToDayForCurrentUser(String day, Long workoutId) {
        // resolve current authenticated username
        Integer userId = null;
        try {
            if (userService != null && userService.isAuthenticated()) {
                var u = userService.getLoggedInUser();
                if (u != null) {
                    userId = u.getId();
                }
            }
        } catch (Throwable ignored) { }

        // fallback: try SecurityContextHolder (legacy path)
        if (userId == null) {
            try {
                org.springframework.security.core.Authentication auth =
                        org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
                    String username = auth.getName();
                    String lookupSql = "SELECT id FROM users WHERE username = ?";
                    try (Connection conn = dataSource.getConnection();
                         PreparedStatement ps = conn.prepareStatement(lookupSql)) {
                        ps.setString(1, username);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                userId = rs.getInt("id");
                            }
                        }
                    }
                }
            } catch (Throwable ignored) { }
        }

        if (userId == null) {
            System.out.println("assignWorkoutToDayForCurrentUser: current user not found");
            return false;
        }

        Integer wid = workoutId == null ? null : workoutId.intValue();

        // Use repository.upsert to persist (keeps DB schema consistent)
        try {
            WeeklyPlan wp = new WeeklyPlan(null, userId, day, wid, "", null);
            repository.upsert(wp);
            return true;
        } catch (Throwable t) {
            System.out.println("Error assigning workout to day: " + t.getMessage());
            return false;
        }
    }
}