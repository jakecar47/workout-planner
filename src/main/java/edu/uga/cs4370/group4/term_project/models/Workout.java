package edu.uga.cs4370.group4.term_project.models;

public class Workout {

    private int id;
    private String name;
    private int userId;
    private String description;
    private String startTime;   
    private String endTime;     
    private String createdAt;   

    // ---------- Constructors ----------
    public Workout() {
        // Required empty constructor
    }

    public Workout(int id,
                   String name,
                   int userId,
                   String description,
                   String startTime,
                   String endTime,
                   String createdAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
    }

    // For creation when ID and createdAt aren't known yet
    public Workout(String name,
                   int userId,
                   String description,
                   String startTime,
                   String endTime) {
        this.name = name;
        this.userId = userId;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // ---------- Getters & Setters ----------
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
