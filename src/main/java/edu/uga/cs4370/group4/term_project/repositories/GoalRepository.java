package edu.uga.cs4370.group4.term_project.repositories;

import edu.uga.cs4370.group4.term_project.models.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GoalRepository {

    private final DataSource dataSource;

    @Autowired
    public GoalRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // GET ALL GOALS FOR USER
    public List<Goal> getGoalsForUser(int userId) {
        List<Goal> goals = new ArrayList<>();

        String sql = """
            SELECT id, description, user_id, exercise_id, created_at
            FROM goals
            WHERE user_id = ?
            ORDER BY created_at DESC
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    Goal goal = new Goal(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getInt("user_id"),
                            rs.getObject("exercise_id", Integer.class),
                            rs.getTimestamp("created_at").toString()   
                    );

                    goals.add(goal);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch goals for user ID " + userId, e);
        }

        return goals;
    }

    // CREATE NEW GOAL
    public boolean createGoal(Goal goal) {

        String sql = "INSERT INTO goals (description, user_id, exercise_id) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, goal.getDescription());
            stmt.setInt(2, goal.getUserId());

            if (goal.getExerciseId() == null)
                stmt.setNull(3, Types.INTEGER);
            else
                stmt.setInt(3, goal.getExerciseId());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error creating goal: " + e.getMessage());
            return false;
        }
    }
}
