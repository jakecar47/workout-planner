package edu.uga.cs4370.group4.term_project.services;

import edu.uga.cs4370.group4.term_project.models.WeeklyPlan;
import edu.uga.cs4370.group4.term_project.repositories.WeeklyPlanRepository;
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

    @Autowired
    public WeeklyPlanService(WeeklyPlanRepository repository, DataSource dataSource) {
        this.repository = repository;
        this.dataSource = dataSource;
    }

    /**
     * Returns a map keyed by day -> { workoutId, notes } for the given user.
     * workoutId will be null if the stored value is 0 (no workout assigned).
     */
    public Map<String, Map<String, Object>> getScheduleForUser(int userId) {
        List<WeeklyPlan> rows = repository.findByUserId(userId);
        Map<String, Map<String, Object>> schedule = new HashMap<>();
        for (WeeklyPlan r : rows) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("workoutId", r.getWorkoutId() == 0 ? null : r.getWorkoutId());
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
        int wid = workoutId == null ? 0 : workoutId;
        WeeklyPlan wp = new WeeklyPlan(null, userId, day, wid, notes, null);
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
        String username = null;
        try {
            // try to get username from Spring Security if present
            Class.forName("org.springframework.security.core.context.SecurityContextHolder");
            try {
                org.springframework.security.core.Authentication auth =
                        org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    username = auth.getName();
                }
            } catch (Throwable t) {
                // ignore - not running with Spring Security
                username = null;
            }
        } catch (ClassNotFoundException ignored) {
            // Spring Security not on classpath
            username = null;
        }

        Integer userId = null;
        if (username != null) {
            String lookupSql = "SELECT id FROM users WHERE username = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(lookupSql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("id");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error looking up current user id: " + e.getMessage());
                return false;
            }
        }

        // If we couldn't resolve user id, fail fast
        if (userId == null) {
            System.out.println("assignWorkoutToDayForCurrentUser: current user not found");
            return false;
        }

        int wId = workoutId == null ? 0 : workoutId.intValue();

        String updateSql = "UPDATE weekly_plans SET workout_id = ? WHERE user_id = ? AND day = ?";
        String insertSql = "INSERT INTO weekly_plans (user_id, day, workout_id) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            // try update
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, wId);
                ps.setInt(2, userId);
                ps.setString(3, day);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    return true;
                }
            }

            // no existing row, insert
            try (PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
                ps2.setInt(1, userId);
                ps2.setString(2, day);
                ps2.setInt(3, wId);
                return ps2.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error assigning workout to day: " + e.getMessage());
            return false;
        }
    }
}
