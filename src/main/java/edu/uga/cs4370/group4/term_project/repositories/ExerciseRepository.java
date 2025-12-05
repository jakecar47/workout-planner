package edu.uga.cs4370.group4.term_project.repositories;

import edu.uga.cs4370.group4.term_project.models.Exercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExerciseRepository {

    private final DataSource dataSource;

    @Autowired
    public ExerciseRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Retrieves all exercises in the system.
     */
    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();

        String sql = """
            SELECT id, name, target_muscle, description, image_path
            FROM exercises
            ORDER BY name ASC
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Exercise exercise = new Exercise(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("target_muscle"),
                        rs.getString("description"),
                        rs.getString("image_path")
                );
                exercises.add(exercise);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch exercises", e);
        }

        return exercises;
    }

    /**
     * Insert a new exercise into the database
     * Returns true if successful, false if duplicate name or failure.
     */
    public boolean createExercise(Exercise exercise) {
        String sql = """
            INSERT INTO exercises (name, target_muscle, description, image_path)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, exercise.getName());
            stmt.setString(2, exercise.getTargetMuscle());
            stmt.setString(3, exercise.getDescription());
            stmt.setString(4, exercise.getImagePath());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // Duplicate name constraint triggers SQL error -> return false
            return false;
        }
    }

    /**
     * Retrieve a single exercise by ID. 
     */
    public Exercise getExerciseById(int id) {
        String sql = """
            SELECT id, name, target_muscle, description, image_path
            FROM exercises
            WHERE id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Exercise(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("target_muscle"),
                            rs.getString("description"),
                            rs.getString("image_path")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch exercise by id", e);
        }

        return null;
    }
}
