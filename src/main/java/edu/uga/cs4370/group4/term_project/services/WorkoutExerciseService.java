package edu.uga.cs4370.group4.term_project.services;

import edu.uga.cs4370.group4.term_project.models.WorkoutExercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkoutExerciseService {

    private final DataSource dataSource;

    @Autowired
    public WorkoutExerciseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ADD EXERCISE TO WORKOUT
    public boolean addExerciseToWorkout(int workoutId,
                                        int exerciseId,
                                        Integer time,
                                        Integer sets,
                                        Integer reps) {

        String sql = """
            INSERT INTO workout_exercises
            (workout_id, exercise_id, time, sets, reps)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workoutId);
            ps.setInt(2, exerciseId);

            if (time == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, time);

            if (sets == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, sets);

            if (reps == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, reps);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error adding exercise to workout: " + e.getMessage());
            return false;
        }
    }

    // GET ALL EXERCISES FOR A WORKOUT
    public List<WorkoutExercise> getExercisesForWorkout(int workoutId) {

        List<WorkoutExercise> list = new ArrayList<>();

        String sql = """
            SELECT
                we.workout_id,
                we.exercise_id,
                e.name AS exercise_name,
                we.time,
                we.sets,
                we.reps
            FROM workout_exercises we
            JOIN exercises e ON we.exercise_id = e.id
            WHERE we.workout_id = ?
            ORDER BY e.name ASC
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workoutId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    WorkoutExercise we = new WorkoutExercise(
                            rs.getInt("workout_id"),
                            rs.getInt("exercise_id"),
                            rs.getString("exercise_name"),
                            rs.getObject("time", Integer.class),   
                            rs.getObject("sets", Integer.class),   
                            rs.getObject("reps", Integer.class)    
                    );

                    list.add(we);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving workout exercises: " + e.getMessage());
        }

        return list;
    }

    // UPDATE WORKOUT EXERCISE
    public boolean updateWorkoutExercise(int workoutId,
                                          int exerciseId,
                                          Integer time,
                                          Integer sets,
                                          Integer reps) {

        String sql = """
            UPDATE workout_exercises
            SET time = ?, sets = ?, reps = ?
            WHERE workout_id = ? AND exercise_id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (time == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, time);

            if (sets == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, sets);

            if (reps == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, reps);

            ps.setInt(4, workoutId);
            ps.setInt(5, exerciseId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating workout exercise: " + e.getMessage());
            return false;
        }
    }

    // DELETE ONE EXERCISE FROM A WORKOUT
    public boolean removeExercise(int workoutId, int exerciseId) {

        String sql = """
            DELETE FROM workout_exercises
            WHERE workout_id = ? AND exercise_id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workoutId);
            ps.setInt(2, exerciseId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting workout exercise: " + e.getMessage());
            return false;
        }
    }

    // DELETE ALL EXERCISES FROM A WORKOUT
    public boolean deleteAllExercisesFromWorkout(int workoutId) {

        String sql = """
            DELETE FROM workout_exercises
            WHERE workout_id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workoutId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error deleting all exercises from workout: " + e.getMessage());
            return false;
        }
    }
}
