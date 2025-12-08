package edu.uga.cs4370.group4.term_project.services;

import edu.uga.cs4370.group4.term_project.models.Exercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExerciseService {

    private final DataSource dataSource;

    @Autowired
    public ExerciseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** -----------------------------------------
     * CREATE A NEW EXERCISE
     * ----------------------------------------- */
    public boolean createExercise(String name, String targetMuscle,
                                  String description, String imagePath) {

        String sql = """
            INSERT INTO exercises (name, target_muscle, description, image_path)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, targetMuscle);
            ps.setString(3, description);
            ps.setString(4, imagePath);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error creating exercise: " + e.getMessage());
            return false;
        }
    }

    /** -----------------------------------------
     * GET ALL EXERCISES 
     * ----------------------------------------- */
    public List<Exercise> getAllExercises() {
        System.out.println("getAllExercises() called");
        List<Exercise> exercises = new ArrayList<>();

        String sql = """
            SELECT id, name, target_muscle, description, image_path
            FROM exercises
            ORDER BY name ASC
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Exercise e = new Exercise(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("target_muscle"),
                        rs.getString("description"),
                        rs.getString("image_path")
                );
                exercises.add(e);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving all exercises: " + e.getMessage());
        }

        return exercises;
    }

    /** -----------------------------------------
     * SEARCH EXERCISES BY NAME 
     * ----------------------------------------- */
    public List<Exercise> searchExercises(String keyword) {
        List<Exercise> exercises = new ArrayList<>();

        String sql = """
            SELECT id, name, target_muscle, description, image_path
            FROM exercises
            WHERE LOWER(name) LIKE ?
            ORDER BY name ASC
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword.toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Exercise e = new Exercise(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("target_muscle"),
                            rs.getString("description"),
                            rs.getString("image_path")
                    );
                    exercises.add(e);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching exercises: " + e.getMessage());
        }

        return exercises;
    }

    /** -----------------------------------------
     * GET EXERCISE BY ID 
     * ----------------------------------------- */
    public Exercise getExerciseById(int id) {
        String sql = """
            SELECT id, name, target_muscle, description, image_path
            FROM exercises
            WHERE id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new Exercise(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("target_muscle"),
                        rs.getString("description"),
                        rs.getString("image_path")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving exercise by ID: " + e.getMessage());
            return null;
        }
    }

    /** -----------------------------------------
     * UPDATE EXERCISE 
     * ----------------------------------------- */
    public boolean updateExercise(int id, String name,
                                  String targetMuscle,
                                  String description,
                                  String imagePath) {

        String sql = """
            UPDATE exercises
            SET name = ?, target_muscle = ?, description = ?, image_path = ?
            WHERE id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, targetMuscle);
            ps.setString(3, description);
            ps.setString(4, imagePath);
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating exercise: " + e.getMessage());
            return false;
        }
    }

    /** -----------------------------------------
     * DELETE EXERCISE
     * ----------------------------------------- */
    public boolean deleteExercise(int id) {
        String sql = "DELETE FROM exercises WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting exercise: " + e.getMessage());
            return false;
        }
    }
}
