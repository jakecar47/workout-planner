package edu.uga.cs4370.group4.term_project.models;

public class WorkoutExercise {

    private int workoutId;
    private int exerciseId;
    private String exerciseName;
    private Integer time;   
    private Integer sets;   
    private Integer reps;   

    public WorkoutExercise() {
    }

    public WorkoutExercise(int workoutId,
                           int exerciseId,
                           String exerciseName,
                           Integer time,
                           Integer sets,
                           Integer reps) {
        this.workoutId = workoutId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.time = time;
        this.sets = sets;
        this.reps = reps;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }
}
