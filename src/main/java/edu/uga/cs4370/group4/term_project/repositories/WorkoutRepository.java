package edu.uga.cs4370.group4.term_project.repositories;

import edu.uga.cs4370.group4.term_project.models.Workout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WorkoutRepository {

    @Autowired
    private DataSource dataSource;

    // INSERT NEW WORKOUT
    public boolean createWorkout(Workout workout) {
        String sql = """
            INSERT INTO workouts (name, user_id, description, startTime, endTime)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, workout.getName());
            stmt.setInt(2, workout.getUserId());
            stmt.setString(3, workout.getDescription());
            stmt.setString(4, workout.getStartTime());
            stmt.setString(5, workout.getEndTime());

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Error creating workout: " + e.getMessage());
            return false;
        }
    }

    // GET ALL WORKOUTS FOR A USER
    public List<Workout> getWorkoutsByUser(int userId) {
        List<Workout> results = new ArrayList<>();

        String sql = "SELECT * FROM workouts WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
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

                    results.add(w);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving workouts: " + e.getMessage());
        }

        return results;
    }

    // GET WORKOUT BY ID
    public Workout getWorkoutById(int id) {
        String sql = "SELECT * FROM workouts WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

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
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving workout: " + e.getMessage());
        }

        return null;
    }
}
