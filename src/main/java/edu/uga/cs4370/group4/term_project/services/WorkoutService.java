package edu.uga.cs4370.group4.term_project.services;

import edu.uga.cs4370.group4.term_project.models.Workout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkoutService {

    private final DataSource dataSource;

    @Autowired
    public WorkoutService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** -----------------------------------------------------
     *  CREATE WORKOUT
     * ----------------------------------------------------- */
    public boolean createWorkout(String name, int userId, String description,
                                 String startTime, String endTime) {

        String sql = "INSERT INTO workouts (name, user_id, description, startTime, endTime) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, userId);
            ps.setString(3, description);
            ps.setString(4, startTime);
            ps.setString(5, endTime);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error creating workout: " + e.getMessage());
            return false;
        }
    }

    /** -----------------------------------------------------
     *  GET ALL WORKOUTS FOR A USER
     * ----------------------------------------------------- */
    public List<Workout> getWorkoutsForUser(int userId) {
        List<Workout> list = new ArrayList<>();

        String sql = """
            SELECT id, name, user_id, description, startTime, endTime, created_at
            FROM workouts
            WHERE user_id = ?
            ORDER BY created_at DESC
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Workout w = new Workout(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("user_id"),
                            rs.getString("description"),
                            rs.getString("startTime"),
                            rs.getString("endTime"),
                            rs.getTimestamp("created_at").toString()
                    );

                    list.add(w);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving workouts for user: " + e.getMessage());
        }

        return list;
    }

    /** -----------------------------------------------------
     *  GET WORKOUT BY ID
     * ----------------------------------------------------- */
    public Workout getWorkoutById(int workoutId) {

        String sql = """
            SELECT id, name, user_id, description, startTime, endTime, created_at
            FROM workouts
            WHERE id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workoutId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new Workout(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("user_id"),
                        rs.getString("description"),
                        rs.getString("startTime"),
                        rs.getString("endTime"),
                        rs.getTimestamp("created_at").toString()
                );
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving workout by ID: " + e.getMessage());
            return null;
        }
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
     *  DELETE WORKOUT
     * ----------------------------------------------------- */
    public boolean deleteWorkout(int workoutId) {

        String sql = "DELETE FROM workouts WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workoutId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting workout: " + e.getMessage());
            return false;
        }
    }
}
