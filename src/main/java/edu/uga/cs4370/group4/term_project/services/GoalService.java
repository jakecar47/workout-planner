package edu.uga.cs4370.group4.term_project.services;

import edu.uga.cs4370.group4.term_project.models.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoalService {

    private final DataSource dataSource;

    @Autowired
    public GoalService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** ---------------------------------------------------------
     * CREATE GOAL
     * --------------------------------------------------------- */
    public boolean createGoal(int userId, String description, Integer exerciseId) {
        String sql = "INSERT INTO goals (description, user_id, exercise_id) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, description);
            ps.setInt(2, userId);

            if (exerciseId == null)
                ps.setNull(3, Types.INTEGER);
            else
                ps.setInt(3, exerciseId);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error creating goal: " + e.getMessage());
            return false;
        }
    }

    /** ---------------------------------------------------------
     * GET GOALS FOR USER
     * --------------------------------------------------------- */
    public List<Goal> getGoalsForUser(int userId) {
        List<Goal> goals = new ArrayList<>();
        // sort completed (1/true) first, then newest created_at
        String sql = "SELECT id, user_id, description, completed, created_at " +
                     "FROM goals WHERE user_id = ? " +
                     "ORDER BY COALESCE(completed, 0) DESC, created_at DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Goal g = new Goal();
                    g.setId(rs.getInt("id"));
                    g.setUserId(rs.getInt("user_id"));
                    g.setDescription(rs.getString("description"));
                    g.setCompleted(rs.getBoolean("completed"));
                    g.setCreatedAt(rs.getTimestamp("created_at").toString());
                    goals.add(g);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }

    // Toggle completion state for a goal (only if it belongs to the user)
    public boolean toggleGoalCompletion(int goalId, int userId) {
        String sql = "UPDATE goals SET completed = NOT IFNULL(completed, 0) WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, goalId);
            ps.setInt(2, userId);
            int updated = ps.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** ---------------------------------------------------------
     * GET GOAL BY ID
     * --------------------------------------------------------- */
    public Goal getGoalById(int goalId) {
        String sql =
            "SELECT id, description, user_id, exercise_id, created_at, completed " +
            "FROM goals WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, goalId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new Goal(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getInt("user_id"),
                        rs.getObject("exercise_id", Integer.class),
                        rs.getTimestamp("created_at").toString()   
                );
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving goal by ID: " + e.getMessage());
            return null;
        }
    }

    /** ---------------------------------------------------------
     * UPDATE GOAL
     * --------------------------------------------------------- */
    public boolean updateGoal(int id, String description, Integer exerciseId) {
        String sql = "UPDATE goals SET description = ?, exercise_id = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, description);

            if (exerciseId == null)
                ps.setNull(2, Types.INTEGER);
            else
                ps.setInt(2, exerciseId);

            ps.setInt(3, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating goal: " + e.getMessage());
            return false;
        }
    }

    /** ---------------------------------------------------------
     * DELETE GOAL
     * --------------------------------------------------------- */
    public boolean deleteGoal(int id) {
        String sql = "DELETE FROM goals WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting goal: " + e.getMessage());
            return false;
        }
    }
}
