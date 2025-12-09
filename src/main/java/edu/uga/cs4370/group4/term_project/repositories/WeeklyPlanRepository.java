package edu.uga.cs4370.group4.term_project.repositories;

import edu.uga.cs4370.group4.term_project.models.WeeklyPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class WeeklyPlanRepository {

    private final DataSource dataSource;

    @Autowired
    public WeeklyPlanRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        ensureTableExists();
    }

    private void ensureTableExists() {
        String ddl = """
            CREATE TABLE IF NOT EXISTS weekly_plan (
              id INT AUTO_INCREMENT,
              user_id INT NOT NULL,
              day VARCHAR(20) NOT NULL,
              workout_id INT NOT NULL,
              notes TEXT,
              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
              PRIMARY KEY (id),
              UNIQUE KEY uq_user_day (user_id, day),
              CONSTRAINT fk_weekly_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure weekly_plan table exists", e);
        }
    }

    public List<WeeklyPlan> findByUserId(int userId) {
        List<WeeklyPlan> plans = new ArrayList<>();
        String sql = """
            SELECT id, user_id, day, workout_id, notes, created_at
            FROM weekly_plan
            WHERE user_id = ?
            ORDER BY FIELD(day,'MON','TUE','WED','THU','FRI','SAT','SUN'), created_at DESC
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch weekly plan for user ID " + userId, e);
        }

        return plans;
    }

    public Optional<WeeklyPlan> findByUserIdAndDay(int userId, String day) {
        String sql = """
            SELECT id, user_id, day, workout_id, notes, created_at
            FROM weekly_plan
            WHERE user_id = ? AND day = ?
            LIMIT 1
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, day);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch weekly plan for user ID " + userId + " day " + day, e);
        }
    }

    public void upsert(WeeklyPlan wp) {
        String sql = """
            INSERT INTO weekly_plan (user_id, day, workout_id, notes)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE workout_id = VALUES(workout_id), notes = VALUES(notes), created_at = CURRENT_TIMESTAMP
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wp.getUserId());
            stmt.setString(2, wp.getDay());

            // bind workout_id as NULL when no workout assigned
            Integer wid = wp.getWorkoutId();
            if (wid != null) {
                stmt.setInt(3, wid);
            } else {
                stmt.setNull(3, Types.INTEGER);
        }

            if (wp.getNotes() == null) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, wp.getNotes());
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to upsert weekly plan entry: " + wp, e);
        }
    }


    public void deleteByUserIdAndDay(int userId, String day) {
        String sql = "DELETE FROM weekly_plan WHERE user_id = ? AND day = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, day);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete weekly plan for user ID " + userId + " day " + day, e);
        }
    }

    private WeeklyPlan mapRow(ResultSet rs) throws SQLException {
        WeeklyPlan w = new WeeklyPlan();
        Object idObj = rs.getObject("id");
        w.setId(idObj == null ? null : rs.getInt("id"));
        w.setUserId(rs.getInt("user_id"));
        w.setDay(rs.getString("day"));
        // If workout_id is null treat as 0 per DDL expectations
        Object widObj = rs.getObject("workout_id");
        w.setWorkoutId(widObj == null ? 0 : rs.getInt("workout_id"));
        w.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) w.setCreatedAt(ts.toLocalDateTime());
        return w;
    }
}