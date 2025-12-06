package edu.uga.cs4370.group4.term_project.models;

public class Goal {

    private int id;
    private String description;
    private int userId;
    private Integer exerciseId; 
    private String createdAt;   // store timestamp as String
    private boolean completed;

    // ---------- Constructors ----------
    public Goal() { }

    // Full constructor for reading from database
    public Goal(int id,
                String description,
                int userId,
                Integer exerciseId,
                String createdAt) {
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.exerciseId = exerciseId;
        this.createdAt = createdAt;
        this.completed = false;
    }

    // Constructor for creating new goals 
    public Goal(String description, int userId, Integer exerciseId, boolean completed) {
        this.description = description;
        this.userId = userId;
        this.exerciseId = exerciseId;
        this.completed = completed;
    }

    // ---------- Getters & Setters ----------
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Integer exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
