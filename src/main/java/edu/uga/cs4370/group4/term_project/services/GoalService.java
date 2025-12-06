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
     * GET ALL GOALS FOR A USER
     * --------------------------------------------------------- */
    public List<Goal> getGoalsForUser(int userId) {
        List<Goal> list = new ArrayList<>();

        String sql =
            "SELECT id, description, user_id, exercise_id, created_at " +
            "FROM goals WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Goal g = new Goal(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getInt("user_id"),
                            rs.getObject("exercise_id", Integer.class),
                            rs.getTimestamp("created_at").toString()   
                    );

                    list.add(g);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving goals: " + e.getMessage());
        }

        return list;
    }

    /** ---------------------------------------------------------
     * GET GOAL BY ID
     * --------------------------------------------------------- */
    public Goal getGoalById(int goalId) {
        String sql =
            "SELECT id, description, user_id, exercise_id, created_at " +
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
